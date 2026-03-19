package com.career.plan.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class RoleDTO {
    
    /**
     * 创建角色请求
     */
    @Data
    public static class CreateRoleRequest {
        @NotBlank(message = "角色名不能为空")
        @Size(max = 50, message = "角色名不能超过 50 个字符")
        private String name;
        
        @Size(max = 255, message = "描述不能超过 255 个字符")
        private String description;
        
        private List<String> permissions;  // 权限名列表
    }
    
    /**
     * 更新角色请求
     */
    @Data
    public static class UpdateRoleRequest {
        @Size(max = 50, message = "角色名不能超过 50 个字符")
        private String name;
        private String description;
        private List<String> permissions;
    }
    
    /**
     * 添加权限到角色请求
     */
    @Data
    public static class AddPermissionRequest {
        @NotBlank(message = "权限名不能为空")
        private String permissionName;
    }
    
    /**
     * 角色响应
     */
    @Data
    public static class RoleResponse {
        private Long id;
        private String name;
        private String description;
        private List<String> permissions;
    }
    
    /**
     * 权限响应
     */
    @Data
    public static class PermissionResponse {
        private Long id;
        private String name;
        private String description;
    }
}
