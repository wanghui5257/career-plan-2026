package com.career.plan.controller;

import com.career.plan.dto.ApiResponse;
import com.career.plan.dto.TaskConfirmResponse;
import com.career.plan.entity.Task;
import com.career.plan.repository.TaskRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Task getTask(@PathVariable Long id) {
        return taskRepository.findById(id).orElse(null);
    }
    
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }
    
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        return taskRepository.save(task);
    }
    
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
    
    @GetMapping("/assignee/{assignee}")
    public List<Task> getByAssignee(@PathVariable String assignee) {
        return taskRepository.findByAssignedTo(assignee);
    }
    
    /**
     * 确认任务
     * POST /api/v1/tasks/{taskId}/confirm
     * 幂等性处理：如果任务已经确认，直接返回成功
     */
    @PostMapping("/{taskId}/confirm")
    public ApiResponse<TaskConfirmResponse> confirmTask(
            @PathVariable Long taskId,
            HttpServletRequest httpRequest) {
        try {
            Long currentUserId = (Long) httpRequest.getAttribute("userId");
            String currentUsername = (String) httpRequest.getAttribute("username");
            if (currentUserId == null) {
                return ApiResponse.error(401, "未授权访问");
            }
            
            Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
            
            // 验证任务是否分配给当前用户
            String assignedTo = task.getAssignedTo();
            if (assignedTo == null || assignedTo.isEmpty()) {
                return ApiResponse.error(403, "该任务未分配给用户");
            }
            
            // 权限验证：任务必须分配给当前用户（按用户名匹配）
            if (!assignedTo.equals(currentUsername)) {
                log.warn("权限拒绝：任务 {} 分配给 {}，当前用户 {}", taskId, assignedTo, currentUsername);
                return ApiResponse.error(403, "权限不足：该任务未分配给您");
            }
            
            // 幂等性处理：如果任务已经确认，直接返回成功
            if (Boolean.TRUE.equals(task.getConfirmed())) {
                log.info("任务 {} 已经确认过，返回幂等响应", taskId);
                return ApiResponse.success("任务已确认", TaskConfirmResponse.fromTask(task));
            }
            
            // 更新确认状态
            task.setConfirmed(true);
            task.setConfirmedAt(LocalDateTime.now());
            taskRepository.save(task);
            
            return ApiResponse.success("任务确认成功", TaskConfirmResponse.fromTask(task));
        } catch (RuntimeException e) {
            if ("任务不存在".equals(e.getMessage())) {
                return ApiResponse.error(404, e.getMessage());
            }
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "服务器内部错误");
        }
    }
}
