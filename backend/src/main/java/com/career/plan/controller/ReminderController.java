package com.career.plan.controller;

import com.career.plan.entity.Reminder;
import com.career.plan.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reminders")
@CrossOrigin(origins = "*")
@Tag(name = "提醒通知", description = "提醒和通知管理 API")
public class ReminderController {
    
    @Autowired
    private ReminderService reminderService;
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取用户提醒列表", description = "获取当前用户的提醒列表")
    public ResponseEntity<?> getUserReminders(@RequestHeader(value = "X-User-Id") Long userId) {
        try {
            List<Reminder> reminders = reminderService.getUserReminders(userId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", reminders
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取提醒失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/unread")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取未读提醒", description = "获取当前用户的未读提醒")
    public ResponseEntity<?> getUnreadReminders(@RequestHeader(value = "X-User-Id") Long userId) {
        try {
            List<Reminder> reminders = reminderService.getUnreadReminders(userId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", reminders
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取未读提醒失败：" + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{id}/read")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "标记提醒为已读", description = "将指定提醒标记为已读")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            Reminder reminder = reminderService.markAsRead(id);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "标记成功",
                "data", reminder
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "标记失败：" + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/read-all")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "标记所有提醒为已读", description = "将当前用户的所有提醒标记为已读")
    public ResponseEntity<?> markAllAsRead(@RequestHeader(value = "X-User-Id") Long userId) {
        try {
            int count = reminderService.markAllAsRead(userId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "标记成功",
                "data", Map.of("markedCount", count)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "标记失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取提醒统计", description = "获取当前用户的提醒统计数据")
    public ResponseEntity<?> getReminderStats(@RequestHeader(value = "X-User-Id") Long userId) {
        try {
            Map<String, Object> stats = reminderService.getReminderStats(userId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取统计失败：" + e.getMessage()
            ));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('PLAN_CREATE', 'ADMIN')")
    @Operation(summary = "创建提醒", description = "创建新的提醒")
    public ResponseEntity<?> createReminder(
            @RequestHeader(value = "X-User-Id") Long userId,
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestParam(defaultValue = "CUSTOM") String type,
            @RequestParam(defaultValue = "IN_APP") String channel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime sendAt) {
        try {
            Reminder reminder = reminderService.createReminder(
                userId, title, content, type, channel, 
                sendAt != null ? sendAt : LocalDateTime.now());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "创建成功",
                "data", reminder
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "创建失败：" + e.getMessage()
            ));
        }
    }
}
