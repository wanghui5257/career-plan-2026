package com.career.plan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getToken() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "admin123");

        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return objectMapper.readTree(response).get("data").get("token").asText();
    }

    @Test
    void testGetPlans_Authenticated() throws Exception {
        String token = getToken();

        mockMvc.perform(get("/api/v1/plans")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    void testGetPlans_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/plans"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreatePlan_Authenticated() throws Exception {
        String token = getToken();

        Map<String, Object> planRequest = new HashMap<>();
        planRequest.put("title", "测试计划");
        planRequest.put("description", "单元测试创建的计划");
        planRequest.put("goal", "学习目标");
        planRequest.put("startDate", LocalDate.now().toString());
        planRequest.put("endDate", LocalDate.now().plusMonths(3).toString());
        planRequest.put("status", "ACTIVE");

        mockMvc.perform(post("/api/v1/plans")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(planRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("测试计划"));
    }

    @Test
    void testCreatePlan_Unauthenticated() throws Exception {
        Map<String, Object> planRequest = new HashMap<>();
        planRequest.put("title", "测试计划");

        mockMvc.perform(post("/api/v1/plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(planRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreatePlan_EmptyTitle() throws Exception {
        String token = getToken();

        Map<String, Object> planRequest = new HashMap<>();
        planRequest.put("title", "");

        mockMvc.perform(post("/api/v1/plans")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(planRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testAddTaskToPlan_Authenticated() throws Exception {
        String token = getToken();

        // 先创建计划
        Map<String, Object> planRequest = new HashMap<>();
        planRequest.put("title", "测试计划");
        planRequest.put("status", "ACTIVE");

        String planResponse = mockMvc.perform(post("/api/v1/plans")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(planRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Long planId = objectMapper.readTree(planResponse).get("id").asLong();

        // 添加任务
        Map<String, Object> taskRequest = new HashMap<>();
        taskRequest.put("title", "测试任务");
        taskRequest.put("description", "计划中的任务");
        taskRequest.put("priority", "HIGH");

        mockMvc.perform(post("/api/v1/plans/" + planId + "/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testBulkImport_Authenticated() throws Exception {
        String token = getToken();

        Map<String, Object> bulkRequest = new HashMap<>();
        bulkRequest.put("title", "批量导入测试");
        bulkRequest.put("description", "批量导入测试计划");
        bulkRequest.put("goal", "批量导入目标");

        mockMvc.perform(post("/api/v1/plans/bulk")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bulkRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
