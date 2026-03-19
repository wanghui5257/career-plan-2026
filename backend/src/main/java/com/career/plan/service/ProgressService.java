package com.career.plan.service;

import com.career.plan.entity.Plan;
import com.career.plan.entity.Progress;
import com.career.plan.entity.Task;
import com.career.plan.repository.PlanRepository;
import com.career.plan.repository.ProgressRepository;
import com.career.plan.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProgressService {
    
    @Autowired
    private ProgressRepository progressRepository;
    
    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    /**
     * 计算并更新计划进度
     */
    public Progress calculatePlanProgress(Long planId) {
        // 获取计划
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在：" + planId));
        
        // 获取计划相关的所有任务
        List<Task> tasks = taskRepository.findAll();
        
        // 统计任务状态
        long completedTasks = tasks.stream()
            .filter(task -> "DONE".equals(task.getStatus()) || "COMPLETED".equals(task.getStatus()))
            .count();
        
        long inProgressTasks = tasks.stream()
            .filter(task -> "IN_PROGRESS".equals(task.getStatus()))
            .count();
        
        long pendingTasks = tasks.stream()
            .filter(task -> "TODO".equals(task.getStatus()) || "PENDING".equals(task.getStatus()))
            .count();
        
        int totalTasks = tasks.size();
        int progressPercentage = totalTasks > 0 ? (int) Math.round((double) completedTasks / totalTasks * 100) : 0;
        
        // 查询现有进度记录
        Progress progress = progressRepository.findTopByPlanIdOrderByUpdatedAtDesc(planId).orElse(new Progress());
        progress.setPlanId(planId);
        progress.setProgressPercentage(progressPercentage);
        progress.setCompletedTasks((int) completedTasks);
        progress.setTotalTasks(totalTasks);
        progress.setPendingTasks((int) pendingTasks);
        progress.setInProgressTasks((int) inProgressTasks);
        
        return progressRepository.save(progress);
    }
    
    /**
     * 获取计划进度
     */
    @Transactional(readOnly = true)
    public Progress getPlanProgress(Long planId) {
        return progressRepository.findTopByPlanIdOrderByUpdatedAtDesc(planId).orElse(null);
    }
    
    /**
     * 获取进度历史
     */
    @Transactional(readOnly = true)
    public List<Progress> getProgressHistory(Long planId) {
        return progressRepository.findByPlanIdOrderByUpdatedAtDesc(planId);
    }
    
    /**
     * 获取所有计划的进度摘要
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getProgressSummary() {
        List<Plan> plans = planRepository.findAll();
        List<Task> allTasks = taskRepository.findAll();
        
        // 总体统计
        long totalPlans = plans.size();
        long totalTasks = allTasks.size();
        long completedTasks = allTasks.stream()
            .filter(task -> "DONE".equals(task.getStatus()) || "COMPLETED".equals(task.getStatus()))
            .count();
        long inProgressTasks = allTasks.stream()
            .filter(task -> "IN_PROGRESS".equals(task.getStatus()))
            .count();
        long pendingTasks = allTasks.stream()
            .filter(task -> "TODO".equals(task.getStatus()) || "PENDING".equals(task.getStatus()))
            .count();
        
        int overallProgress = totalTasks > 0 ? (int) Math.round((double) completedTasks / totalTasks * 100) : 0;
        
        // 按状态分组
        Map<String, Long> tasksByStatus = allTasks.stream()
            .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));
        
        // 构建响应
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPlans", totalPlans);
        summary.put("totalTasks", totalTasks);
        summary.put("completedTasks", completedTasks);
        summary.put("inProgressTasks", inProgressTasks);
        summary.put("pendingTasks", pendingTasks);
        summary.put("overallProgress", overallProgress);
        summary.put("tasksByStatus", tasksByStatus);
        summary.put("lastUpdated", LocalDateTime.now());
        
        return summary;
    }
    
    /**
     * 更新所有计划的进度
     */
    public Map<String, Progress> updateAllPlansProgress() {
        List<Plan> plans = planRepository.findAll();
        Map<String, Progress> results = new HashMap<>();
        
        for (Plan plan : plans) {
            try {
                Progress progress = calculatePlanProgress(plan.getId());
                results.put("plan-" + plan.getId(), progress);
            } catch (Exception e) {
                results.put("plan-" + plan.getId() + "-error", null);
            }
        }
        
        return results;
    }
}
