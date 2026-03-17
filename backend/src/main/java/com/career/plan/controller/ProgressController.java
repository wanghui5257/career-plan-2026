package com.career.plan.controller;

import com.career.plan.dto.ProgressDTO;
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
@Tag(name = "进度管理", description = "计划进度查询和统计 API")
public class ProgressController {
    
    @Autowired
    private ProgressService progressService;
    
    @GetMapping("/plan/{planId}")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取计划进度", description = "获取指定计划的进度信息")
    public ResponseEntity<?> getPlanProgress(@PathVariable Long planId) {
        try {
            ProgressDTO.PlanProgressResponse response = progressService.getPlanProgress(planId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取进度失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取进度摘要", description = "获取所有计划的整体进度摘要")
    public ResponseEntity<?> getProgressSummary() {
        try {
            ProgressDTO.ProgressSummary summary = progressService.getProgressSummary();
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", summary
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取摘要失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/history/{planId}")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取进度历史", description = "获取指定计划的进度变化历史")
    public ResponseEntity<?> getProgressHistory(@PathVariable Long planId) {
        try {
            List<ProgressDTO.ProgressHistoryItem> history = progressService.getProgressHistory(planId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", history
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取历史失败：" + e.getMessage()
            ));
        }
    }
}
