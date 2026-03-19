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
class ReminderControllerTest {

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
    void testGetUserReminders_Authenticated() throws Exception {
        String token = getToken();

        mockMvc.perform(get("/api/v1/reminders")
                .header("X-User-Id", "1")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetUserReminders_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/reminders")
                .header("X-User-Id", "1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUnreadReminders_Authenticated() throws Exception {
        String token = getToken();

        mockMvc.perform(get("/api/v1/reminders/unread")
                .header("X-User-Id", "1")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testMarkAsRead_Authenticated() throws Exception {
        String token = getToken();

        // 先创建一个提醒
        mockMvc.perform(post("/api/v1/reminders")
                .header("X-User-Id", "1")
                .header("Authorization", "Bearer " + token)
                .param("title", "测试提醒")
                .param("content", "测试内容")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk());

        // 标记为已读（使用 ID 1）
        mockMvc.perform(post("/api/v1/reminders/1/read")
                .header("X-User-Id", "1")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetReminderStats_Authenticated() throws Exception {
        String token = getToken();

        mockMvc.perform(get("/api/v1/reminders/stats")
                .header("X-User-Id", "1")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testCreateReminder_Authenticated() throws Exception {
        String token = getToken();

        mockMvc.perform(post("/api/v1/reminders")
                .header("X-User-Id", "1")
                .header("Authorization", "Bearer " + token)
                .param("title", "测试创建提醒")
                .param("content", "测试内容")
                .param("type", "CUSTOM")
                .param("channel", "IN_APP")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
