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
class TaskControllerTest {

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

        return objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void testGetTasks_Authenticated() throws Exception {
        String token = getToken();

        mockMvc.perform(get("/api/v1/tasks")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetTasks_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateTask_Authenticated() throws Exception {
        String token = getToken();

        Map<String, Object> taskRequest = new HashMap<>();
        taskRequest.put("title", "测试任务");
        taskRequest.put("description", "单元测试创建的任务");
        taskRequest.put("status", "TODO");
        taskRequest.put("priority", "MEDIUM");

        mockMvc.perform(post("/api/v1/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.title").value("测试任务"));
    }

    @Test
    void testCreateTask_Unauthenticated() throws Exception {
        Map<String, Object> taskRequest = new HashMap<>();
        taskRequest.put("title", "测试任务");

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
            .andExpect(status().isUnauthorized());
    }
}
