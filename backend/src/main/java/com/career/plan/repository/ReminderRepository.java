package com.career.plan.repository;

import com.career.plan.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    
    /**
     * 根据用户 ID 查询提醒
     */
    List<Reminder> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 查询待发送的提醒
     */
    List<Reminder> findByStatusAndSendAtBefore(String status, LocalDateTime sendAt);
    
    /**
     * 查询用户未读提醒
     */
    List<Reminder> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * 根据任务 ID 查询提醒
     */
    List<Reminder> findByTaskId(Long taskId);
    
    /**
     * 根据计划 ID 查询提醒
     */
    List<Reminder> findByPlanId(Long planId);
}
