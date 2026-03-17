package com.career.plan.controller;

import com.career.plan.entity.Task;
import com.career.plan.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "任务管理", description = "任务 CRUD API")
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('TASK_VIEW', 'ADMIN')")
    @Operation(summary = "获取任务列表", description = "获取所有任务")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TASK_VIEW', 'ADMIN')")
    @Operation(summary = "获取单个任务", description = "根据 ID 获取任务详情")
    public Task getTask(@PathVariable Long id) {
        return taskRepository.findById(id).orElse(null);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('TASK_CREATE', 'ADMIN')")
    @Operation(summary = "创建任务", description = "创建新任务")
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TASK_UPDATE', 'ADMIN')")
    @Operation(summary = "更新任务", description = "更新现有任务")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        return taskRepository.save(task);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TASK_DELETE', 'ADMIN')")
    @Operation(summary = "删除任务", description = "根据 ID 删除任务")
    public void deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
    
    @GetMapping("/assignee/{assignee}")
    @PreAuthorize("hasAnyAuthority('TASK_VIEW', 'ADMIN')")
    @Operation(summary = "按负责人查询任务", description = "获取指定负责人的任务列表")
    public List<Task> getByAssignee(@PathVariable String assignee) {
        return taskRepository.findByAssignedTo(assignee);
    }
}
