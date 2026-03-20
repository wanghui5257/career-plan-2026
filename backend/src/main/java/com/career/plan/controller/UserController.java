package com.career.plan.controller;

import com.career.plan.dto.ApiResponse;
import com.career.plan.dto.ChangePasswordRequest;
import com.career.plan.dto.UserProfileResponse;
import com.career.plan.dto.UserProfileUpdateRequest;
import com.career.plan.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 修改密码
     * PUT /api/v1/user/password
     */
    @PutMapping("/password")
    public ApiResponse<Void> changePassword(
            @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return ApiResponse.error(401, "未授权访问");
            }
            
            userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            return ApiResponse.success("密码修改成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "服务器内部错误");
        }
    }

    /**
     * 获取用户资料（顾问专用）
     * GET /api/v1/user/{userId}/profile
     */
    @GetMapping("/{userId}/profile")
    public ApiResponse<UserProfileResponse> getUserProfile(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {
        try {
            // 验证当前用户角色（顾问专用）
            Long currentUserId = (Long) httpRequest.getAttribute("userId");
            if (currentUserId == null) {
                return ApiResponse.error(401, "未授权访问");
            }
            
            // 获取用户角色并验证是否为顾问
            String userRole = (String) httpRequest.getAttribute("userRole");
            if (userRole == null || !"顾问".equals(userRole)) {
                return ApiResponse.error(403, "权限不足：仅顾问可以查看用户资料");
            }
            
            UserProfileResponse profile = userService.getUserProfile(userId);
            return ApiResponse.success("查询成功", profile);
        } catch (RuntimeException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "服务器内部错误");
        }
    }

    /**
     * 更新用户资料
     * PUT /api/v1/user/profile
     */
    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(
            @RequestBody UserProfileUpdateRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return ApiResponse.error(401, "未授权访问");
            }
            
            UserProfileResponse profile = userService.updateProfile(userId, request);
            return ApiResponse.success("资料更新成功", profile);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "服务器内部错误");
        }
    }

    /**
     * 获取角色列表
     * GET /api/v1/user/roles
     */
    @GetMapping("/roles")
    public ApiResponse<List<Map<String, Object>>> getRoles() {
        try {
            List<Map<String, Object>> roles = Arrays.asList(
                createRoleMap(1L, "ADMIN", "管理员"),
                createRoleMap(2L, "PLAN_CREATOR", "计划创建者"),
                createRoleMap(3L, "SUPERVISOR", "顾问"),
                createRoleMap(4L, "EXECUTOR", "执行者"),
                createRoleMap(5L, "WORKER", "工作者")
            );
            return ApiResponse.success("查询成功", roles);
        } catch (Exception e) {
            return ApiResponse.error(500, "服务器内部错误");
        }
    }

    private Map<String, Object> createRoleMap(Long id, String code, String name) {
        return Map.of(
            "id", id,
            "code", code,
            "name", name
        );
    }
}
