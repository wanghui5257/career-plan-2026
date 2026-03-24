package com.career.plan.controller;

import com.career.plan.entity.Progress;
import com.career.plan.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PLAN_VIEW')")
    public List<Progress> getAllProgress() {
        return progressService.getAllProgress();
    }
    
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PLAN_VIEW')")
    public List<Progress> getProgressList() {
        return progressService.getAllProgress();
    }
    
    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PLAN_VIEW')")
    public Map<String, Object> getProgressSummary() {
        return progressService.getProgressSummary();
    }
    
    @GetMapping("/plan/{planId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PLAN_VIEW')")
    public List<Progress> getByPlanId(@PathVariable Long planId) {
        return progressService.getByPlanId(planId);
    }
    
    @GetMapping("/history/{planId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PLAN_VIEW')")
    public List<Progress> getProgressHistory(@PathVariable Long planId) {
        return progressService.getByPlanId(planId);
    }
    
    /**
     * 获取所有计划的进度历史（用于图表展示）
     */
    @GetMapping("/history")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PLAN_VIEW')")
    public List<Progress> getAllProgressHistory() {
        return progressService.getAllProgressHistory();
    }
    
    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PLAN_VIEW')")
    public List<Progress> getByTaskId(@PathVariable Long taskId) {
        return progressService.getByTaskId(taskId);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROGRESS_CREATOR')")
    public Progress createProgress(@RequestBody Progress progress) {
        return progressService.createProgress(progress);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROGRESS_CREATOR')")
    public Progress updateProgress(@PathVariable Long id, @RequestBody Progress progress) {
        return progressService.updateProgress(id, progress);
    }
    
    /**
     * Alias endpoint for progress-reports (for compatibility)
     */
    @GetMapping("/progress-reports")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PLAN_VIEW')")
    public List<Progress> getProgressReports() {
        return progressService.getAllProgress();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROGRESS_CREATOR')")
    public void deleteProgress(@PathVariable Long id) {
        progressService.deleteProgress(id);
    }
}
