package com.career.plan.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 计划进度实体
 * 用于记录和追踪计划的整体进度
 */
@Data
@Entity
@Table(name = "progress")
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "plan_id", nullable = false)
    private Long planId;
    
    @Column(name = "progress_percentage")
    private Double progressPercentage;  // 0-100
    
    @Column(name = "completed_tasks")
    private Integer completedTasks;
    
    @Column(name = "total_tasks")
    private Integer totalTasks;
    
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
