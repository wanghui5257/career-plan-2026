package com.career.plan.controller;

import com.career.plan.dto.ApiResponse;
import com.career.plan.dto.ChangePasswordRequest;
import com.career.plan.dto.UserProfileResponse;
import com.career.plan.dto.UserProfileUpdateRequest;
import com.career.plan.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
            
            // 密码长度验证（上限 100 字符，防止 DoS 攻击）
            if (request.getNewPassword() == null || request.getNewPassword().length() < 8) {
                return ApiResponse.error(400, "密码长度至少 8 位");
            }
            if (request.getNewPassword().length() > 100) {
                return ApiResponse.error(400, "密码长度不能超过 100 字符");
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
     * 获取当前用户资料（所有用户可访问）
     * GET /api/v1/user/profile
     */
    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getCurrentUserProfile(
            HttpServletRequest httpRequest) {
        try {
            Long currentUserId = (Long) httpRequest.getAttribute("userId");
            if (currentUserId == null) {
                return ApiResponse.error(401, "未授权访问");
            }
            
            UserProfileResponse profile = userService.getUserProfile(currentUserId);
            return ApiResponse.success("查询成功", profile);
        } catch (RuntimeException e) {
            return ApiResponse.error(404, e.getMessage());
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
            @PathVariable String userId,
            HttpServletRequest httpRequest) {
        try {
            // 验证当前用户角色（顾问专用）
            Long currentUserId = (Long) httpRequest.getAttribute("userId");
            if (currentUserId == null) {
                return ApiResponse.error(401, "未授权访问");
            }
            
            // 支持 "current" 路径参数，允许用户查看自己的资料
            Long targetUserId;
            if ("current".equals(userId)) {
                targetUserId = currentUserId;
            } else {
                // 验证是否为顾问（查看他人资料需要顾问权限）
                String userRole = (String) httpRequest.getAttribute("userRole");
                if (userRole == null || !"SUPERVISOR".equals(userRole)) {
                    return ApiResponse.error(403, "权限不足：仅顾问可以查看用户资料");
                }
                targetUserId = Long.parseLong(userId);
            }
            
            UserProfileResponse profile = userService.getUserProfile(targetUserId);
            return ApiResponse.success("查询成功", profile);
        } catch (NumberFormatException e) {
            return ApiResponse.error(400, "无效的用户 ID");
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
            
            // 字段长度验证
            if (request.getName() != null && request.getName().length() > 200) {
                return ApiResponse.error(400, "name 字段长度不能超过 200 字符");
            }
            if (request.getPhone() != null && request.getPhone().length() > 50) {
                return ApiResponse.error(400, "phone 字段长度不能超过 50 字符");
            }
            if (request.getCompany() != null && request.getCompany().length() > 200) {
                return ApiResponse.error(400, "company 字段长度不能超过 200 字符");
            }
            
            UserProfileResponse profile = userService.updateProfile(userId, request);
            return ApiResponse.success("资料更新成功", profile);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "服务器内部错误");
        }
    }
}
