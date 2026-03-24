package com.career.plan.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String name;
    private String phone;
    private String company;
    private String background;
    private String goals;
    private String email;
}
