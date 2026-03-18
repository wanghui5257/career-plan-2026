package com.career.plan.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String background;
    private String goals;
    private String email;
}
