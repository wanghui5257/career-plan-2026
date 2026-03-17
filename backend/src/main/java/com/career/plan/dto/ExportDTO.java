package com.career.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

public class ExportDTO {
    
    /**
     * 导出请求
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExportRequest {
        private String format;  // CSV, EXCEL, JSON
        private boolean includeTasks;
        private boolean includeProgress;
    }
    
    /**
     * 导出响应
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExportResponse {
        private String fileId;
        private String fileName;
        private String format;
        private Long planId;
        private String planName;
        private LocalDateTime generatedAt;
        private LocalDateTime expiresAt;
        private String downloadUrl;
    }
    
    /**
     * 导出历史项
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExportHistoryItem {
        private String fileId;
        private String fileName;
        private String format;
        private Long planId;
        private LocalDateTime generatedAt;
        private LocalDateTime expiresAt;
        private Boolean isExpired;
    }
    
    /**
     * 导出数据（JSON 格式）
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExportData {
        private Long planId;
        private String planName;
        private String planDescription;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<TaskData> tasks;
        private ProgressData progress;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskData {
        private Long taskId;
        private String title;
        private String description;
        private String status;
        private String priority;
        private LocalDateTime dueDate;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProgressData {
        private Double progressPercentage;
        private Integer completedTasks;
        private Integer totalTasks;
    }
}
