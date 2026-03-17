package com.career.plan.controller;

import com.career.plan.dto.PlanDTO;
import com.career.plan.entity.Plan;
import com.career.plan.entity.Task;
import com.career.plan.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/plans")
@CrossOrigin(origins = "*")
@Tag(name = "计划管理", description = "计划 CRUD 和任务管理 API")
public class PlanController {
    
    @Autowired
    private PlanService planService;
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('PLAN_CREATE', 'ADMIN')")
    @Operation(summary = "创建计划", description = "创建新的职业发展计划")
    public ResponseEntity<?> createPlan(@Valid @RequestBody PlanDTO.CreatePlanRequest request,
                                        @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                        @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        try {
            Plan plan = planService.createPlan(request, userId, tenantId);
            return ResponseEntity.ok(planService.buildPlanResponse(plan));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "创建计划失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取计划列表", description = "获取计划列表，支持分页和筛选")
    public ResponseEntity<?> getPlans(@RequestParam(required = false) Long userId,
                                      @RequestParam(required = false) String tenantId,
                                      @RequestParam(required = false) String status) {
        try {
            List<Plan> plans = planService.getPlans(userId, tenantId, status);
            List<PlanDTO.PlanResponse> responses = plans.stream()
                .map(planService::buildPlanResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取计划列表失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取单个计划", description = "根据 ID 获取计划详情")
    public ResponseEntity<?> getPlan(@PathVariable Long id) {
        try {
            Plan plan = planService.getPlanById(id);
            if (plan == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(planService.buildPlanResponse(plan));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取计划失败：" + e.getMessage()
            ));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PLAN_UPDATE', 'PLAN_CREATE', 'ADMIN')")
    @Operation(summary = "更新计划", description = "更新现有计划")
    public ResponseEntity<?> updatePlan(@PathVariable Long id,
                                        @Valid @RequestBody PlanDTO.UpdatePlanRequest request) {
        try {
            Plan plan = planService.updatePlan(id, request);
            return ResponseEntity.ok(planService.buildPlanResponse(plan));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "更新计划失败：" + e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PLAN_DELETE', 'ADMIN')")
    @Operation(summary = "删除计划", description = "根据 ID 删除计划")
    public ResponseEntity<?> deletePlan(@PathVariable Long id) {
        try {
            planService.deletePlan(id);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "计划删除成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "删除计划失败：" + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{id}/tasks")
    @PreAuthorize("hasAnyAuthority('TASK_CREATE', 'PLAN_CREATE', 'ADMIN')")
    @Operation(summary = "添加任务到计划", description = "向指定计划添加新任务")
    public ResponseEntity<?> addTaskToPlan(@PathVariable Long id,
                                           @Valid @RequestBody PlanDTO.AddTaskRequest request,
                                           @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            Task task = planService.addTaskToPlan(id, request, userId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "任务添加成功",
                "data", Map.of(
                    "taskId", task.getId(),
                    "title", task.getTitle()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "添加任务失败：" + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyAuthority('PLAN_CREATE', 'TASK_CREATE', 'ADMIN')")
    @Operation(summary = "批量导入计划和任务", description = "一次性创建计划并添加多个任务")
    public ResponseEntity<?> bulkImport(@Valid @RequestBody PlanDTO.BulkImportRequest request,
                                        @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                        @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        try {
            PlanDTO.PlanResponse response = planService.bulkImport(request, userId, tenantId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "批量导入成功",
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "批量导入失败：" + e.getMessage()
            ));
        }
    }
}
