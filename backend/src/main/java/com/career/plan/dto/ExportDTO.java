package com.career.plan.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExportDTO {
    
    /**
     * 导出请求
     */
    @Data
    public static class ExportRequest {
        private String format;  // CSV, EXCEL, JSON
        private Long planId;
        private Long taskId;
    }
    
    /**
     * 导出响应
     */
    @Data
    public static class ExportResponse {
        private String fileId;
        private String fileName;
        private String format;
        private Long fileSize;
        private String downloadUrl;
        private LocalDateTime generatedAt;
        private LocalDateTime expiresAt;
    }
    
    /**
     * 计划导出数据
     */
    @Data
    public static class PlanExportData {
        private Long planId;
        private String planName;
        private String description;
        private Integer progress;
        private LocalDateTime createdAt;
        private List<TaskExportData> tasks;
    }
    
    /**
     * 任务导出数据
     */
    @Data
    public static class TaskExportData {
        private Long taskId;
        private String title;
        private String description;
        private String status;
        private String priority;
        private Integer progress;
        private String assignee;
        private LocalDateTime dueDate;
    }
    
    /**
     * 导出历史记录
     */
    @Data
    public static class ExportHistoryItem {
        private String fileId;
        private String fileName;
        private String format;
        private Long fileSize;
        private LocalDateTime generatedAt;
        private LocalDateTime expiresAt;
        private String status;  // READY, EXPIRED, DELETED
    }
}
