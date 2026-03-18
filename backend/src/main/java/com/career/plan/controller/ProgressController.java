package com.career.plan.controller;

import com.career.plan.entity.Progress;
import com.career.plan.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/progress")
@CrossOrigin(origins = "*")
public class ProgressController {
    
    @Autowired
    private ProgressService progressService;
    
    @GetMapping
    public List<Progress> getAllProgress() {
        return progressService.getAllProgress();
    }
    
    @GetMapping("/summary")
    public Map<String, Object> getProgressSummary() {
        return progressService.getProgressSummary();
    }
    
    @GetMapping("/plan/{planId}")
    public List<Progress> getByPlanId(@PathVariable Long planId) {
        return progressService.getByPlanId(planId);
    }
    
    @GetMapping("/history/{planId}")
    public List<Progress> getProgressHistory(@PathVariable Long planId) {
        return progressService.getByPlanId(planId);
    }
    
    @GetMapping("/task/{taskId}")
    public List<Progress> getByTaskId(@PathVariable Long taskId) {
        return progressService.getByTaskId(taskId);
    }
    
    @PostMapping
    public Progress createProgress(@RequestBody Progress progress) {
        return progressService.createProgress(progress);
    }
    
    @PutMapping("/{id}")
    public Progress updateProgress(@PathVariable Long id, @RequestBody Progress progress) {
        return progressService.updateProgress(id, progress);
    }
    
    @DeleteMapping("/{id}")
    public void deleteProgress(@PathVariable Long id) {
        progressService.deleteProgress(id);
    }
}
