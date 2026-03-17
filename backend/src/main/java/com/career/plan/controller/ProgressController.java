package com.career.plan.controller;

import com.career.plan.entity.Progress;
import com.career.plan.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/progress")
@CrossOrigin(origins = "*")
@Tag(name = "进度管理", description = "进度计算和查询 API")
public class ProgressController {
    
    @Autowired
    private ProgressService progressService;
    
    @GetMapping("/plan/{planId}")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取计划进度", description = "获取指定计划的进度信息")
    public ResponseEntity<?> getPlanProgress(@PathVariable Long planId) {
        try {
            // 先计算最新进度
            Progress progress = progressService.calculatePlanProgress(planId);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取进度失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取进度摘要", description = "获取所有计划的总体进度摘要")
    public ResponseEntity<?> getProgressSummary() {
        try {
            Map<String, Object> summary = progressService.getProgressSummary();
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", summary
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取进度摘要失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/history/{planId}")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取进度历史", description = "获取指定计划的进度历史记录")
    public ResponseEntity<?> getProgressHistory(@PathVariable Long planId) {
        try {
            List<Progress> history = progressService.getProgressHistory(planId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", history
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取进度历史失败：" + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/calculate/{planId}")
    @PreAuthorize("hasAnyAuthority('PLAN_UPDATE', 'ADMIN')")
    @Operation(summary = "计算计划进度", description = "重新计算指定计划的进度")
    public ResponseEntity<?> calculatePlanProgress(@PathVariable Long planId) {
        try {
            Progress progress = progressService.calculatePlanProgress(planId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "计算成功",
                "data", progress
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "计算进度失败：" + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/calculate/all")
    @PreAuthorize("hasAnyAuthority('PLAN_UPDATE', 'ADMIN')")
    @Operation(summary = "计算所有计划进度", description = "重新计算所有计划的进度")
    public ResponseEntity<?> calculateAllPlansProgress() {
        try {
            Map<String, Progress> results = progressService.updateAllPlansProgress();
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "计算成功",
                "data", results
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "计算所有计划进度失败：" + e.getMessage()
            ));
        }
    }
}
