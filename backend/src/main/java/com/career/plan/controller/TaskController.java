package com.career.plan.controller;

import com.career.plan.entity.Task;
import com.career.plan.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
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
}
