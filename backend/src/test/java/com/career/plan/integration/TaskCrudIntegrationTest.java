package com.career.plan.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 任务管理集成测试
 * 测试完整的任务 CRUD 操作流程
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.Order.class)
@DisplayName("任务管理集成测试")
class TaskCrudIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String authToken = null;
    private static Long createdTaskId = null;

    /**
     * 获取认证 Token
     */
    private String getAuthToken() throws Exception {
        if (authToken == null) {
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("username", "admin");
            loginRequest.put("password", "admin123");

            MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);
            authToken = (String) responseMap.get("token");
        }
        return authToken;
    }

    @Test
    @Order(1)
    @DisplayName("1. 获取任务列表 - 成功")
    void testGetTaskList() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/v1/tasks")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("2. 获取单个任务 - 成功")
    void testGetTaskById() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/v1/tasks/1")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").exists());
    }

    @Test
    @Order(3)
    @DisplayName("3. 获取不存在的任务 - 失败")
    void testGetNonExistentTask() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/v1/tasks/99999")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @Order(4)
    @DisplayName("4. 创建新任务 - 成功")
    void testCreateTask() throws Exception {
        String token = getAuthToken();

        Map<String, Object> taskRequest = new HashMap<>();
        taskRequest.put("title", "集成测试任务-" + System.currentTimeMillis());
        taskRequest.put("description", "这是一个通过集成测试创建的任务");
        taskRequest.put("status", "PENDING");
        taskRequest.put("priority", "MEDIUM");
        taskRequest.put("userId", 1);

        MvcResult result = mockMvc.perform(post("/api/v1/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("创建成功"))
            .andExpect(jsonPath("$.data.id").exists())
            .andReturn();

        // 保存创建的任务 ID 供后续测试使用
        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        createdTaskId = ((Number) data.get("id")).longValue();

        assertThat(createdTaskId).isNotNull();
    }

    @Test
    @Order(5)
    @DisplayName("5. 更新任务 - 成功")
    void testUpdateTask() throws Exception {
        String token = getAuthToken();
        
        if (createdTaskId == null) {
            // 如果前面创建失败，跳过此测试
            return;
        }

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("title", "更新后的任务标题");
        updateRequest.put("description", "更新后的任务描述");
        updateRequest.put("status", "IN_PROGRESS");

        mockMvc.perform(put("/api/v1/tasks/" + createdTaskId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("更新成功"))
            .andExpect(jsonPath("$.data.title").value("更新后的任务标题"))
            .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    @Order(6)
    @DisplayName("6. 验证任务更新结果")
    void testVerifyTaskUpdate() throws Exception {
        String token = getAuthToken();
        
        if (createdTaskId == null) {
            return;
        }

        mockMvc.perform(get("/api/v1/tasks/" + createdTaskId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.title").value("更新后的任务标题"))
            .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    @Order(7)
    @DisplayName("7. 删除任务 - 成功")
    void testDeleteTask() throws Exception {
        String token = getAuthToken();
        
        if (createdTaskId == null) {
            return;
        }

        mockMvc.perform(delete("/api/v1/tasks/" + createdTaskId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("删除成功"));

        // 验证任务已被删除
        mockMvc.perform(get("/api/v1/tasks/" + createdTaskId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    @DisplayName("8. 创建任务 - 缺少必填字段失败")
    void testCreateTaskWithMissingFields() throws Exception {
        String token = getAuthToken();

        Map<String, Object> taskRequest = new HashMap<>();
        // 缺少 title 字段
        taskRequest.put("description", "缺少标题的任务");
        taskRequest.put("status", "PENDING");

        mockMvc.perform(post("/api/v1/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    @DisplayName("9. 未授权访问任务接口失败")
    void testAccessWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
            .andExpect(status().isForbidden());
    }
}
