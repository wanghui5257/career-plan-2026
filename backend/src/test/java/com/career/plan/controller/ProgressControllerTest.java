package com.career.plan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProgressControllerTest {

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
    void testGetProgressSummary_Authenticated() throws Exception {
        String token = getToken();

        mockMvc.perform(get("/api/v1/progress/summary")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetProgressSummary_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/progress/summary"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetPlanProgress_Authenticated() throws Exception {
        String token = getToken();

        // 先创建一个计划
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

        // 获取计划进度
        mockMvc.perform(get("/api/v1/progress/plan/" + planId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    void testCalculatePlanProgress_Authenticated() throws Exception {
        String token = getToken();

        // 创建计划
        Map<String, Object> planRequest = new HashMap<>();
        planRequest.put("title", "测试计算进度");
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

        // 计算进度
        mockMvc.perform(post("/api/v1/progress/calculate/" + planId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testCalculateAllPlansProgress_Authenticated() throws Exception {
        String token = getToken();

        mockMvc.perform(post("/api/v1/progress/calculate/all")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
