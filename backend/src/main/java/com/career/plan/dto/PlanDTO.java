package com.career.plan.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class PlanDTO {
    
    /**
     * 创建计划请求
     */
    @Data
    public static class CreatePlanRequest {
        @NotBlank(message = "计划标题不能为空")
        @Size(max = 255, message = "计划标题不能超过 255 个字符")
        private String title;
        
        private String description;
        private String goal;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;  // DRAFT, ACTIVE, COMPLETED, ARCHIVED
    }
    
    /**
     * 更新计划请求
     */
    @Data
    public static class UpdatePlanRequest {
        @Size(max = 255, message = "计划标题不能超过 255 个字符")
        private String title;
        private String description;
        private String goal;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
    }
    
    /**
     * 添加任务到计划请求
     */
    @Data
    public static class AddTaskRequest {
        @NotBlank(message = "任务标题不能为空")
        private String title;
        private String description;
        private String priority;  // LOW, MEDIUM, HIGH
        private LocalDate dueDate;
        private String status;  // TODO, IN_PROGRESS, DONE
    }
    
    /**
     * 批量导入请求
     */
    @Data
    public static class BulkImportRequest {
        @NotBlank(message = "计划标题不能为空")
        private String title;
        private String description;
        private String goal;
        private LocalDate startDate;
        private LocalDate endDate;
        private java.util.List<AddTaskRequest> tasks;
    }
    
    /**
     * 计划响应
     */
    @Data
    public static class PlanResponse {
        private Long id;
        private String tenantId;
        private Long userId;
        private String title;
        private String description;
        private String goal;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private Long createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private java.util.List<TaskInfo> tasks;
        
        @Data
        public static class TaskInfo {
            private Long id;
            private String title;
            private String status;
            private Integer progress;
        }
    }
}
