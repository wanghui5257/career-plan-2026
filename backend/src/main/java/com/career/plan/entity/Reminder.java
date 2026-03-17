package com.career.plan.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reminders")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "task_id")
    private Long taskId;
    
    @Column(name = "plan_id")
    private Long planId;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false, length = 50)
    private String type;  // TASK_DUE, TASK_OVERDUE, PROGRESS_REMINDER, CUSTOM
    
    @Column(nullable = false, length = 50)
    private String channel;  // IN_APP, EMAIL, DINGTALK
    
    @Column(nullable = false, length = 50)
    private String status;  // PENDING, SENT, READ
    
    @Column(name = "send_at")
    private LocalDateTime sendAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
