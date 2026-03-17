package com.career.plan.service;

import com.career.plan.dto.ProgressDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProgressService {
    
    @Autowired
    private ProgressRepository progressRepository;
    
    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    /**
     * 获取计划进度
     */
    @Transactional(readOnly = true)
    public ProgressDTO.PlanProgressResponse getPlanProgress(Long planId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在"));
        
        // 获取该计划下的所有任务
        List<Task> tasks = taskRepository.findByPlanId(planId);
        
        // 计算进度
        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream()
            .filter(task -> "DONE".equals(task.getStatus()) || "COMPLETED".equals(task.getStatus()))
            .count();
        
        double progressPercentage = totalTasks > 0 
            ? (double) completedTasks / totalTasks * 100 
            : 0.0;
        
        // 保存或更新进度记录
        Progress progress = saveProgress(planId, progressPercentage, completedTasks, totalTasks);
        
        return new ProgressDTO.PlanProgressResponse(
            planId,
            plan.getTitle(),
            progressPercentage,
            completedTasks,
            totalTasks,
            progress.getUpdatedAt()
        );
    }
    
    /**
     * 获取进度摘要
     */
    @Transactional(readOnly = true)
    public ProgressDTO.ProgressSummary getProgressSummary() {
        List<Plan> allPlans = planRepository.findAll();
        
        int totalPlans = allPlans.size();
        int activePlans = (int) allPlans.stream()
            .filter(plan -> "ACTIVE".equals(plan.getStatus()))
            .count();
        
        List<ProgressDTO.PlanProgressResponse> planProgresses = new ArrayList<>();
        int totalTasks = 0;
        int totalCompletedTasks = 0;
        
        for (Plan plan : allPlans) {
            ProgressDTO.PlanProgressResponse progress = getPlanProgress(plan.getId());
            planProgresses.add(progress);
            totalTasks += progress.getTotalTasks();
            totalCompletedTasks += progress.getCompletedTasks();
        }
        
        double averageProgress = totalPlans > 0 
            ? planProgresses.stream().mapToDouble(ProgressDTO.PlanProgressResponse::getProgressPercentage).average().orElse(0.0)
            : 0.0;
        
        // 获取最近的 5 个计划
        List<ProgressDTO.PlanProgressResponse> recentPlans = planProgresses.stream()
            .limit(5)
            .collect(Collectors.toList());
        
        return new ProgressDTO.ProgressSummary(
            totalPlans,
            activePlans,
            averageProgress,
            totalTasks,
            totalCompletedTasks,
            recentPlans
        );
    }
    
    /**
     * 获取进度历史
     */
    @Transactional(readOnly = true)
    public List<ProgressDTO.ProgressHistoryItem> getProgressHistory(Long planId) {
        List<Progress> history = progressRepository.findByPlanIdOrderByUpdatedAtDesc(planId);
        
        List<ProgressDTO.ProgressHistoryItem> result = new ArrayList<>();
        Progress previous = null;
        
        for (Progress current : history) {
            String changeDesc = "";
            if (previous != null) {
                double change = current.getProgressPercentage() - previous.getProgressPercentage();
                if (change > 0) {
                    changeDesc = String.format("进度提升 %.1f%%", change);
                } else if (change < 0) {
                    changeDesc = String.format("进度下降 %.1f%%", Math.abs(change));
                } else {
                    changeDesc = "进度无变化";
                }
            }
            
            result.add(new ProgressDTO.ProgressHistoryItem(
                current.getId(),
                current.getProgressPercentage(),
                current.getCompletedTasks(),
                current.getTotalTasks(),
                current.getUpdatedAt(),
                changeDesc
            ));
            
            previous = current;
        }
        
        return result;
    }
    
    /**
     * 保存或更新进度记录
     */
    @Transactional
    public Progress saveProgress(Long planId, double progressPercentage, int completedTasks, int totalTasks) {
        Optional<Progress> existingOpt = progressRepository.findTopByPlanIdOrderByUpdatedAtDesc(planId);
        
        // 如果进度没有变化，不创建新记录
        if (existingOpt.isPresent()) {
            Progress existing = existingOpt.get();
            if (existing.getProgressPercentage().equals(progressPercentage)) {
                return existing;
            }
        }
        
        Progress progress = existingOpt.orElse(new Progress());
        progress.setPlanId(planId);
        progress.setProgressPercentage(progressPercentage);
        progress.setCompletedTasks(completedTasks);
        progress.setTotalTasks(totalTasks);
        
        return progressRepository.save(progress);
    }
    
    /**
     * 更新计划进度（当任务状态变更时调用）
     */
    @Transactional
    public ProgressDTO.PlanProgressResponse updatePlanProgress(Long planId) {
        return getPlanProgress(planId);
    }
}
