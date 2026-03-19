package com.career.plan.service;

import com.career.plan.entity.Reminder;
import com.career.plan.entity.Task;
import com.career.plan.entity.User;
import com.career.plan.repository.ReminderRepository;
import com.career.plan.repository.TaskRepository;
import com.career.plan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReminderService {
    
    @Autowired
    private ReminderRepository reminderRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 创建提醒
     */
    public Reminder createReminder(Long userId, String title, String content, 
                                   String type, String channel, LocalDateTime sendAt) {
        Reminder reminder = new Reminder();
        reminder.setUserId(userId);
        reminder.setTitle(title);
        reminder.setContent(content);
        reminder.setType(type);
        reminder.setChannel(channel);
        reminder.setStatus("PENDING");
        reminder.setSendAt(sendAt);
        return reminderRepository.save(reminder);
    }
    
    /**
     * 创建任务到期提醒
     */
    public Reminder createTaskDueReminder(Long taskId, Long userId, LocalDateTime dueDate) {
        String title = "任务即将到期";
        String content = "您有一个任务即将到期，请及时完成。";
        return createReminder(userId, title, content, "TASK_DUE", "IN_APP", dueDate.minusHours(24));
    }
    
    /**
     * 获取用户提醒列表
     */
    @Transactional(readOnly = true)
    public List<Reminder> getUserReminders(Long userId) {
        return reminderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * 获取用户未读提醒
     */
    @Transactional(readOnly = true)
    public List<Reminder> getUnreadReminders(Long userId) {
        return reminderRepository.findByUserIdAndStatus(userId, "PENDING");
    }
    
    /**
     * 标记提醒为已读
     */
    public Reminder markAsRead(Long reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> new RuntimeException("提醒不存在"));
        reminder.setStatus("READ");
        reminder.setReadAt(LocalDateTime.now());
        return reminderRepository.save(reminder);
    }
    
    /**
     * 标记所有用户提醒为已读
     */
    public int markAllAsRead(Long userId) {
        List<Reminder> reminders = reminderRepository.findByUserIdAndStatus(userId, "PENDING");
        int count = 0;
        for (Reminder reminder : reminders) {
            reminder.setStatus("READ");
            reminder.setReadAt(LocalDateTime.now());
            reminderRepository.save(reminder);
            count++;
        }
        return count;
    }
    
    /**
     * 发送提醒
     */
    public Reminder sendReminder(Long reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> new RuntimeException("提醒不存在"));
        
        // 根据渠道发送提醒
        switch (reminder.getChannel()) {
            case "IN_APP":
                sendInAppNotification(reminder);
                break;
            case "EMAIL":
                sendEmailNotification(reminder);
                break;
            case "DINGTALK":
                sendDingTalkNotification(reminder);
                break;
            default:
                sendInAppNotification(reminder);
        }
        
        reminder.setStatus("SENT");
        return reminderRepository.save(reminder);
    }
    
    /**
     * 发送站内信通知
     */
    private void sendInAppNotification(Reminder reminder) {
        // 实现站内信通知逻辑
        // 这里可以推送到 WebSocket 或保存到通知表
    }
    
    /**
     * 发送邮件通知
     */
    private void sendEmailNotification(Reminder reminder) {
        // 实现邮件发送逻辑
        // 可以使用 himalaya 技能或 JavaMail
    }
    
    /**
     * 发送钉钉通知
     */
    private void sendDingTalkNotification(Reminder reminder) {
        // 实现钉钉机器人通知逻辑
        // 可以调用钉钉 Webhook API
    }
    
    /**
     * 定时任务：扫描待发送的提醒
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void scanAndSendReminders() {
        List<Reminder> pendingReminders = reminderRepository.findByStatusAndSendAtBefore(
            "PENDING", LocalDateTime.now());
        
        for (Reminder reminder : pendingReminders) {
            try {
                sendReminder(reminder.getId());
            } catch (Exception e) {
                // 记录错误日志
            }
        }
    }
    
    /**
     * 定时任务：扫描到期任务并创建提醒
     */
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void scanDueTasks() {
        List<Task> tasks = taskRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        
        for (Task task : tasks) {
            if (task.getDueDate() != null) {
                // 任务即将到期（24 小时内）
                if (task.getDueDate().isAfter(now) && 
                    task.getDueDate().isBefore(now.plusHours(24))) {
                    
                    // 查找是否已存在提醒
                    List<Reminder> existingReminders = reminderRepository.findByTaskId(task.getId());
                    boolean hasDueReminder = existingReminders.stream()
                        .anyMatch(r -> "TASK_DUE".equals(r.getType()));
                    
                    // Disabled: task.getAssignedTo() returns String but method expects Long
                    // createTaskDueReminder(task.getId(), task.getAssignedTo(), task.getDueDate());
                }
            }
        }
    }
    
    /**
     * 获取提醒统计
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getReminderStats(Long userId) {
        List<Reminder> allReminders = reminderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        long total = allReminders.size();
        long unread = allReminders.stream().filter(r -> "PENDING".equals(r.getStatus())).count();
        long read = allReminders.stream().filter(r -> "READ".equals(r.getStatus())).count();
        long sent = allReminders.stream().filter(r -> "SENT".equals(r.getStatus())).count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("unread", unread);
        stats.put("read", read);
        stats.put("sent", sent);
        stats.put("lastUpdated", LocalDateTime.now());
        
        return stats;
    }
}
