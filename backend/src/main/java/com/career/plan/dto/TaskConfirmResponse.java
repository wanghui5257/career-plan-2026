package com.career.plan.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskConfirmResponse {
    private Long id;
    private String title;
    private Boolean confirmed;
    private LocalDateTime confirmedAt;
    
    public static TaskConfirmResponse fromTask(com.career.plan.entity.Task task) {
        TaskConfirmResponse response = new TaskConfirmResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setConfirmed(task.getConfirmed());
        response.setConfirmedAt(task.getConfirmedAt());
        return response;
    }
}
