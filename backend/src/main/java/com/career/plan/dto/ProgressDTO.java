package com.career.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

public class ProgressDTO {
    
    /**
     * 计划进度响应
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlanProgressResponse {
        private Long planId;
        private String planTitle;
        private Double progressPercentage;  // 0-100
        private Integer completedTasks;
        private Integer totalTasks;
        private LocalDateTime lastUpdatedAt;
    }
    
    /**
     * 进度摘要响应
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProgressSummary {
        private Integer totalPlans;
        private Integer activePlans;
        private Double averageProgress;  // 平均进度百分比
        private Integer totalTasks;
        private Integer completedTasks;
        private List<PlanProgressResponse> recentPlans;
    }
    
    /**
     * 进度历史项
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProgressHistoryItem {
        private Long id;
        private Double progressPercentage;
        private Integer completedTasks;
        private Integer totalTasks;
        private LocalDateTime updatedAt;
        private String changeDescription;  // 进度变化描述
    }
}
