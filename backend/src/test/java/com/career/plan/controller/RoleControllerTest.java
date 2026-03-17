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
class RoleControllerTest {

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
    void testGetRoles_Authenticated() throws Exception {
        String token = getToken();

        mockMvc.perform(get("/api/v1/roles")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    void testGetRoles_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/roles"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetPermissions_Authenticated() throws Exception {
        String token = getToken();

        mockMvc.perform(get("/api/v1/roles/permissions")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    void testInitializeDefaultRoles() throws Exception {
        // 初始化接口是公开的
        mockMvc.perform(post("/api/v1/roles/init")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testCreateRole_Authenticated() throws Exception {
        String token = getToken();

        Map<String, Object> roleRequest = new HashMap<>();
        roleRequest.put("name", "TEST_ROLE");
        roleRequest.put("description", "测试角色");

        mockMvc.perform(post("/api/v1/roles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("TEST_ROLE"));
    }

    @Test
    void testCreateRole_EmptyName() throws Exception {
        String token = getToken();

        Map<String, Object> roleRequest = new HashMap<>();
        roleRequest.put("name", "");

        mockMvc.perform(post("/api/v1/roles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleRequest)))
            .andExpect(status().isBadRequest());
    }
}
