package com.career.plan.service;

import com.career.plan.dto.RoleDTO;
import com.career.plan.entity.Permission;
import com.career.plan.entity.Role;
import com.career.plan.repository.PermissionRepository;
import com.career.plan.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    /**
     * 创建角色
     */
    public Role createRole(RoleDTO.CreateRoleRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        
        // 添加权限
        if (request.getPermissions() != null) {
            List<Permission> permissions = permissionRepository.findAllById(
                request.getPermissions().stream()
                    .map(this::getPermissionIdByName)
                    .filter(id -> id != null)
                    .collect(Collectors.toList())
            );
            role.setPermissions(new HashSet<>(permissions));
        }
        
        return roleRepository.save(role);
    }
    
    /**
     * 根据 ID 获取角色
     */
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }
    
    /**
     * 根据名称获取角色
     */
    @Transactional(readOnly = true)
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }
    
    /**
     * 获取角色列表
     */
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * 更新角色
     */
    public Role updateRole(Long id, RoleDTO.UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("角色不存在"));
        
        if (request.getName() != null) {
            role.setName(request.getName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getPermissions() != null) {
            List<Permission> permissions = permissionRepository.findAllById(
                request.getPermissions().stream()
                    .map(this::getPermissionIdByName)
                    .filter(id -> id != null)
                    .collect(Collectors.toList())
            );
            role.setPermissions(new HashSet<>(permissions));
        }
        
        return roleRepository.save(role);
    }
    
    /**
     * 删除角色
     */
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
    
    /**
     * 添加权限到角色
     */
    public Role addPermissionToRole(Long roleId, String permissionName) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("角色不存在"));
        
        Permission permission = permissionRepository.findByName(permissionName)
            .orElseThrow(() -> new RuntimeException("权限不存在：" + permissionName));
        
        role.getPermissions().add(permission);
        return roleRepository.save(role);
    }
    
    /**
     * 获取所有权限
     */
    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAllByOrderByCreatedAtAsc();
    }
    
    /**
     * 初始化默认角色和权限
     */
    @Transactional
    public void initializeDefaultRoles() {
        // 创建默认权限
        String[] defaultPermissions = {
            "PLAN_CREATE", "PLAN_VIEW", "PLAN_UPDATE", "PLAN_DELETE",
            "TASK_CREATE", "TASK_VIEW", "TASK_UPDATE", "TASK_DELETE",
            "PROGRESS_VIEW", "PROGRESS_CREATE",
            "USER_MANAGE", "ROLE_MANAGE"
        };
        
        for (String permName : defaultPermissions) {
            permissionRepository.findByName(permName).orElseGet(() -> {
                Permission permission = new Permission();
                permission.setName(permName);
                permission.setDescription(permName + " permission");
                return permissionRepository.save(permission);
            });
        }
        
        // 创建默认角色
        createDefaultRole("ADMIN", "管理员", 
            "PLAN_CREATE", "PLAN_VIEW", "PLAN_UPDATE", "PLAN_DELETE",
            "TASK_CREATE", "TASK_VIEW", "TASK_UPDATE", "TASK_DELETE",
            "PROGRESS_VIEW", "PROGRESS_CREATE",
            "USER_MANAGE", "ROLE_MANAGE");
        
        createDefaultRole("PLAN_CREATOR", "计划创建者",
            "PLAN_CREATE", "PLAN_VIEW", "PLAN_UPDATE",
            "TASK_CREATE", "TASK_VIEW");
        
        createDefaultRole("SUPERVISOR", "监督执行者",
            "PLAN_VIEW", "TASK_VIEW", "PROGRESS_VIEW");
        
        createDefaultRole("EXECUTOR", "任务执行者",
            "PLAN_VIEW", "TASK_VIEW", "TASK_UPDATE", "PROGRESS_CREATE");
        
        createDefaultRole("WORKER", "Worker",
            "PLAN_VIEW", "TASK_VIEW", "TASK_UPDATE", "PROGRESS_CREATE");
    }
    
    private Role createDefaultRole(String name, String description, String... permissions) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            
            List<Permission> permissionList = new ArrayList<>();
            for (String permName : permissions) {
                permissionRepository.findByName(permName).ifPresent(permissionList::add);
            }
            role.setPermissions(new HashSet<>(permissionList));
            
            return roleRepository.save(role);
        });
    }
    
    private Long getPermissionIdByName(String name) {
        return permissionRepository.findByName(name)
            .map(Permission::getId)
            .orElse(null);
    }
}
