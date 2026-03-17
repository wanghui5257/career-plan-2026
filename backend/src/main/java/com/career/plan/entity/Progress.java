package com.career.plan.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "progress_tracking")
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "plan_id", nullable = false)
    private Long planId;
    
    @Column(name = "progress_percentage")
    private Integer progressPercentage;  // 0-100
    
    @Column(name = "completed_tasks")
    private Integer completedTasks;
    
    @Column(name = "total_tasks")
    private Integer totalTasks;
    
    @Column(name = "pending_tasks")
    private Integer pendingTasks;
    
    @Column(name = "in_progress_tasks")
    private Integer inProgressTasks;
    
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
