package com.career.plan.service;

import com.career.plan.dto.PlanDTO;
import com.career.plan.entity.Plan;
import com.career.plan.entity.Task;
import com.career.plan.repository.PlanRepository;
import com.career.plan.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlanService {
    
    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    /**
     * 创建计划
     */
    public Plan createPlan(PlanDTO.CreatePlanRequest request, Long userId, String tenantId) {
        Plan plan = new Plan();
        plan.setTitle(request.getTitle());
        plan.setDescription(request.getDescription());
        plan.setGoal(request.getGoal());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        plan.setUserId(userId);
        plan.setTenantId(tenantId);
        plan.setCreatedBy(userId);
        return planRepository.save(plan);
    }
    
    /**
     * 根据 ID 获取计划
     */
    @Transactional(readOnly = true)
    public Plan getPlanById(Long id) {
        return planRepository.findById(id).orElse(null);
    }
    
    /**
     * 获取计划列表（支持筛选）
     */
    @Transactional(readOnly = true)
    public List<Plan> getPlans(Long userId, String tenantId, String status) {
        if (status != null) {
            if (userId != null) {
                return planRepository.findByUserIdAndStatus(userId, status);
            } else {
                return planRepository.findByStatus(status);
            }
        } else {
            if (userId != null) {
                return planRepository.findByUserIdAndTenantId(userId, tenantId);
            } else {
                return planRepository.findByTenantId(tenantId);
            }
        }
    }
    
    /**
     * 更新计划
     */
    public Plan updatePlan(Long id, PlanDTO.UpdatePlanRequest request) {
        Plan plan = planRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("计划不存在"));
        
        if (request.getTitle() != null) {
            plan.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription());
        }
        if (request.getGoal() != null) {
            plan.setGoal(request.getGoal());
        }
        if (request.getStartDate() != null) {
            plan.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            plan.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            plan.setStatus(request.getStatus());
        }
        
        return planRepository.save(plan);
    }
    
    /**
     * 删除计划
     */
    public void deletePlan(Long id) {
        planRepository.deleteById(id);
    }
    
    /**
     * 添加任务到计划
     */
    public Task addTaskToPlan(Long planId, PlanDTO.AddTaskRequest request, Long userId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在"));
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        task.setStatus(request.getStatus() != null ? request.getStatus() : "TODO");
        task.setDueDate(request.getDueDate() != null ? request.getDueDate().atStartOfDay() : null);
        task.setAssignedTo(userId != null ? userId.toString() : null);
        task.setProgress(0);
        
        return taskRepository.save(task);
    }
    
    /**
     * 批量导入计划和任务
     */
    public PlanDTO.PlanResponse bulkImport(PlanDTO.BulkImportRequest request, Long userId, String tenantId) {
        // 创建计划
        PlanDTO.CreatePlanRequest createRequest = new PlanDTO.CreatePlanRequest();
        createRequest.setTitle(request.getTitle());
        createRequest.setDescription(request.getDescription());
        createRequest.setGoal(request.getGoal());
        createRequest.setStartDate(request.getStartDate());
        createRequest.setEndDate(request.getEndDate());
        createRequest.setStatus("ACTIVE");
        
        Plan plan = createPlan(createRequest, userId, tenantId);
        
        // 添加任务
        if (request.getTasks() != null) {
            for (PlanDTO.AddTaskRequest taskRequest : request.getTasks()) {
                addTaskToPlan(plan.getId(), taskRequest, userId);
            }
        }
        
        // 返回完整计划信息
        return buildPlanResponse(plan);
    }
    
    /**
     * 构建计划响应对象
     */
    @Transactional(readOnly = true)
    public PlanDTO.PlanResponse buildPlanResponse(Plan plan) {
        PlanDTO.PlanResponse response = new PlanDTO.PlanResponse();
        response.setId(plan.getId());
        response.setTenantId(plan.getTenantId());
        response.setUserId(plan.getUserId());
        response.setTitle(plan.getTitle());
        response.setDescription(plan.getDescription());
        response.setGoal(plan.getGoal());
        response.setStartDate(plan.getStartDate());
        response.setEndDate(plan.getEndDate());
        response.setStatus(plan.getStatus());
        response.setCreatedBy(plan.getCreatedBy());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());
        
        // 加载关联的任务
        List<Task> tasks = taskRepository.findAll();
        List<PlanDTO.PlanResponse.TaskInfo> taskInfos = tasks.stream()
            .map(task -> {
                PlanDTO.PlanResponse.TaskInfo info = new PlanDTO.PlanResponse.TaskInfo();
                info.setId(task.getId());
                info.setTitle(task.getTitle());
                info.setStatus(task.getStatus());
                info.setProgress(task.getProgress());
                return info;
            })
            .collect(Collectors.toList());
        response.setTasks(taskInfos);
        
        return response;
    }
}
