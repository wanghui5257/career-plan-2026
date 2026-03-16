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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 进度报告集成测试
 * 测试完整的进度报告创建、查询、更新流程
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.Order.class)
@DisplayName("进度报告集成测试")
class ProgressReportIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String authToken = null;
    private static Long createdReportId = null;

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
    @DisplayName("1. 获取进度报告列表 - 成功")
    void testGetProgressReportList() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/v1/progress-reports")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("2. 创建进度报告 - 成功")
    void testCreateProgressReport() throws Exception {
        String token = getAuthToken();

        Map<String, Object> reportRequest = new HashMap<>();
        reportRequest.put("userId", 1);
        reportRequest.put("taskId", 1);
        reportRequest.put("content", "今日完成进度报告集成测试的编写");
        reportRequest.put("workHours", 2.5);
        reportRequest.put("reportDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));

        MvcResult result = mockMvc.perform(post("/api/v1/progress-reports")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("创建成功"))
            .andExpect(jsonPath("$.data.id").exists())
            .andReturn();

        // 保存创建的报告 ID 供后续测试使用
        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        createdReportId = ((Number) data.get("id")).longValue();

        assertThat(createdReportId).isNotNull();
        assertThat(createdReportId).isGreaterThan(0);
    }

    @Test
    @Order(3)
    @DisplayName("3. 获取单个进度报告 - 成功")
    void testGetProgressReportById() throws Exception {
        String token = getAuthToken();
        
        if (createdReportId == null) {
            return;
        }

        mockMvc.perform(get("/api/v1/progress-reports/" + createdReportId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value(createdReportId.intValue()))
            .andExpect(jsonPath("$.data.content").exists());
    }

    @Test
    @Order(4)
    @DisplayName("4. 获取不存在的进度报告 - 失败")
    void testGetNonExistentProgressReport() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/v1/progress-reports/99999")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @Order(5)
    @DisplayName("5. 更新进度报告 - 成功")
    void testUpdateProgressReport() throws Exception {
        String token = getAuthToken();
        
        if (createdReportId == null) {
            return;
        }

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("content", "更新后的进度报告内容 - 已完成集成测试");
        updateRequest.put("workHours", 3.0);

        mockMvc.perform(put("/api/v1/progress-reports/" + createdReportId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("更新成功"))
            .andExpect(jsonPath("$.data.content").value("更新后的进度报告内容 - 已完成集成测试"))
            .andExpect(jsonPath("$.data.workHours").value(3.0));
    }

    @Test
    @Order(6)
    @DisplayName("6. 按用户查询进度报告 - 成功")
    void testGetProgressReportsByUserId() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/v1/progress-reports/user/1")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(7)
    @DisplayName("7. 按任务查询进度报告 - 成功")
    void testGetProgressReportsByTaskId() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/v1/progress-reports/task/1")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(8)
    @DisplayName("8. 按日期范围查询进度报告 - 成功")
    void testGetProgressReportsByDateRange() throws Exception {
        String token = getAuthToken();

        String startDate = LocalDate.now().minusDays(7).format(DateTimeFormatter.ISO_DATE);
        String endDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        mockMvc.perform(get("/api/v1/progress-reports")
                .header("Authorization", "Bearer " + token)
                .param("startDate", startDate)
                .param("endDate", endDate))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(9)
    @DisplayName("9. 创建进度报告 - 缺少必填字段失败")
    void testCreateProgressReportWithMissingFields() throws Exception {
        String token = getAuthToken();

        Map<String, Object> reportRequest = new HashMap<>();
        // 缺少 userId 和 content 字段
        reportRequest.put("taskId", 1);
        reportRequest.put("workHours", 1.0);

        mockMvc.perform(post("/api/v1/progress-reports")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(10)
    @DisplayName("10. 删除进度报告 - 成功")
    void testDeleteProgressReport() throws Exception {
        String token = getAuthToken();
        
        if (createdReportId == null) {
            return;
        }

        mockMvc.perform(delete("/api/v1/progress-reports/" + createdReportId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("删除成功"));

        // 验证报告已被删除
        mockMvc.perform(get("/api/v1/progress-reports/" + createdReportId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(11)
    @DisplayName("11. 未授权访问进度报告接口失败")
    void testAccessWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/progress-reports"))
            .andExpect(status().isForbidden());
    }
}
