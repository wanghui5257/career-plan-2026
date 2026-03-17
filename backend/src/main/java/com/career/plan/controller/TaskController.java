package com.career.plan.controller;

import com.career.plan.entity.Task;
import com.career.plan.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @GetMapping
    @PreAuthorize("hasRole('PLAN_VIEW')")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PLAN_VIEW')")
    public Task getTask(@PathVariable Long id) {
        return taskRepository.findById(id).orElse(null);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('PLAN_CREATOR')")
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PLAN_CREATOR')")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        return taskRepository.save(task);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLAN_CREATOR')")
    public void deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
    
    @GetMapping("/assignee/{assignee}")
    @PreAuthorize("hasRole('PLAN_VIEW')")
    public List<Task> getByAssignee(@PathVariable String assignee) {
        return taskRepository.findByAssignedTo(assignee);
    }
}
