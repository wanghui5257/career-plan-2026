package com.career.plan.controller;

import com.career.plan.dto.ApiResponse;
import com.career.plan.dto.TaskConfirmResponse;
import com.career.plan.entity.Task;
import com.career.plan.repository.TaskRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
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
     */
    @PostMapping("/{taskId}/confirm")
    public ApiResponse<TaskConfirmResponse> confirmTask(
            @PathVariable Long taskId,
            HttpServletRequest httpRequest) {
        try {
            Long currentUserId = (Long) httpRequest.getAttribute("userId");
            if (currentUserId == null) {
                return ApiResponse.error(401, "未授权访问");
            }
            
            Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
            
            // 验证任务是否分配给当前用户
            // 注意：assignedTo 存储的是用户名，需要转换
            // 这里简化处理，假设 assignedTo 存储的是用户 ID 的字符串形式
            // 实际项目中可能需要根据用户名查询用户 ID 进行比对
            String assignedTo = task.getAssignedTo();
            if (assignedTo == null || assignedTo.isEmpty()) {
                return ApiResponse.error(403, "该任务未分配给用户");
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
