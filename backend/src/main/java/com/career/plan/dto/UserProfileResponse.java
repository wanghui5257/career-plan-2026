package com.career.plan.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String background;
    private String goals;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static UserProfileResponse fromUser(com.career.plan.entity.User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole() != null ? user.getRole().getName() : null);
        response.setBackground(user.getBackground());
        response.setGoals(user.getGoals());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
