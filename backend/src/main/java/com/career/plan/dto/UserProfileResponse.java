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
        response.setRole(roleIdToRoleName(user.getRoleId()));
        response.setBackground(user.getBackground());
        response.setGoals(user.getGoals());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
    
    private static String roleIdToRoleName(Long roleId) {
        if (roleId == null) return null;
        switch (roleId.intValue()) {
            case 1: return "ADMIN";
            case 2: return "PLAN_CREATOR";
            case 3: return "SUPERVISOR";
            case 4: return "EXECUTOR";
            case 5: return "WORKER";
            default: return "UNKNOWN";
        }
    }
}
