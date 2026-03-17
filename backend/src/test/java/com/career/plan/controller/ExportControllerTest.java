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
class ExportControllerTest {

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
    void testExportPlan_JSON() throws Exception {
        String token = getToken();

        // 先创建一个计划
        Map<String, Object> planRequest = new HashMap<>();
        planRequest.put("title", "测试导出计划");
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

        // 导出计划为 JSON
        mockMvc.perform(post("/api/v1/export/plan/" + planId)
                .header("Authorization", "Bearer " + token)
                .param("format", "JSON"))
            .andExpect(status().isOk());
    }

    @Test
    void testExportPlan_CSV() throws Exception {
        String token = getToken();

        // 创建计划
        Map<String, Object> planRequest = new HashMap<>();
        planRequest.put("title", "测试 CSV 导出");
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

        // 导出计划为 CSV
        mockMvc.perform(post("/api/v1/export/plan/" + planId)
                .header("Authorization", "Bearer " + token)
                .param("format", "CSV"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetExportHistory_Authenticated() throws Exception {
        String token = getToken();

        mockMvc.perform(get("/api/v1/export/history")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    void testGetExportHistory_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/export/history"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testExportPlan_Unauthenticated() throws Exception {
        Map<String, Object> planRequest = new HashMap<>();
        planRequest.put("title", "测试计划");

        mockMvc.perform(post("/api/v1/export/plan/1")
                .param("format", "JSON"))
            .andExpect(status().isUnauthorized());
    }
}
