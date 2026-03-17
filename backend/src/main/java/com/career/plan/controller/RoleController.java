package com.career.plan.controller;

import com.career.plan.dto.RoleDTO;
import com.career.plan.entity.Permission;
import com.career.plan.entity.Role;
import com.career.plan.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/roles")
@CrossOrigin(origins = "*")
@Tag(name = "角色权限管理", description = "角色和权限管理 API")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    @PostMapping
    @Operation(summary = "创建角色", description = "创建新的角色")
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleDTO.CreateRoleRequest request) {
        try {
            Role role = roleService.createRole(request);
            return ResponseEntity.ok(buildRoleResponse(role));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "创建角色失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping
    @Operation(summary = "获取角色列表", description = "获取所有角色")
    public ResponseEntity<?> getRoles() {
        try {
            List<Role> roles = roleService.getAllRoles();
            List<RoleDTO.RoleResponse> responses = roles.stream()
                .map(this::buildRoleResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取角色列表失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取单个角色", description = "根据 ID 获取角色详情")
    public ResponseEntity<?> getRole(@PathVariable Long id) {
        try {
            Role role = roleService.getRoleById(id);
            if (role == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(buildRoleResponse(role));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取角色失败：" + e.getMessage()
            ));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新角色", description = "更新现有角色")
    public ResponseEntity<?> updateRole(@PathVariable Long id,
                                        @Valid @RequestBody RoleDTO.UpdateRoleRequest request) {
        try {
            Role role = roleService.updateRole(id, request);
            return ResponseEntity.ok(buildRoleResponse(role));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "更新角色失败：" + e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "根据 ID 删除角色")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "角色删除成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "删除角色失败：" + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{id}/permissions")
    @Operation(summary = "添加权限到角色", description = "向指定角色添加权限")
    public ResponseEntity<?> addPermissionToRole(@PathVariable Long id,
                                                  @Valid @RequestBody RoleDTO.AddPermissionRequest request) {
        try {
            Role role = roleService.addPermissionToRole(id, request.getPermissionName());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "权限添加成功",
                "data", buildRoleResponse(role)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "添加权限失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/permissions")
    @Operation(summary = "获取权限列表", description = "获取所有权限")
    public ResponseEntity<?> getPermissions() {
        try {
            List<Permission> permissions = roleService.getAllPermissions();
            List<RoleDTO.PermissionResponse> responses = permissions.stream()
                .map(perm -> {
                    RoleDTO.PermissionResponse response = new RoleDTO.PermissionResponse();
                    response.setId(perm.getId());
                    response.setName(perm.getName());
                    response.setDescription(perm.getDescription());
                    return response;
                })
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取权限列表失败：" + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/init")
    @Operation(summary = "初始化默认角色和权限", description = "创建系统默认的角色和权限")
    public ResponseEntity<?> initializeDefaultRoles() {
        try {
            roleService.initializeDefaultRoles();
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "默认角色和权限初始化成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "初始化失败：" + e.getMessage()
            ));
        }
    }
    
    private RoleDTO.RoleResponse buildRoleResponse(Role role) {
        RoleDTO.RoleResponse response = new RoleDTO.RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        
        if (role.getPermissions() != null) {
            List<String> permissionNames = role.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
            response.setPermissions(permissionNames);
        }
        
        return response;
    }
}
