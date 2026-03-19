package com.career.plan.controller;

import com.career.plan.entity.Plan;
import com.career.plan.entity.Task;
import com.career.plan.repository.PlanRepository;
import com.career.plan.repository.ProgressRepository;
import com.career.plan.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProgressControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProgressRepository progressRepository;
    
    private Long testPlanId;
    
    @BeforeEach
    void setUp() {
        // 清理现有数据
        progressRepository.deleteAll();
        taskRepository.deleteAll();
        planRepository.deleteAll();
        
        // 创建测试计划
        Plan plan = new Plan();
        plan.setTitle("测试计划");
        plan.setDescription("用于进度测试");
        plan.setStartDate(LocalDate.now());
        plan.setEndDate(LocalDate.now().plusMonths(3));
        plan.setStatus("ACTIVE");
        plan = planRepository.save(plan);
        testPlanId = plan.getId();
        
        // 创建测试任务
        Task task1 = new Task();
        task1.setTitle("任务 1");
        task1.setPlanId(plan.getId());
        task1.setStatus("DONE");
        taskRepository.save(task1);
        
        Task task2 = new Task();
        task2.setTitle("任务 2");
        task2.setPlanId(plan.getId());
        task2.setStatus("IN_PROGRESS");
        taskRepository.save(task2);
        
        Task task3 = new Task();
        task3.setTitle("任务 3");
        task3.setPlanId(plan.getId());
        task3.setStatus("TODO");
        taskRepository.save(task3);
    }
    
    @Test
    @WithMockUser(authorities = "PLAN_VIEW")
    void testGetPlanProgress() throws Exception {
        mockMvc.perform(get("/api/v1/progress/plan/{planId}", testPlanId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.planId").value(testPlanId))
            .andExpect(jsonPath("$.data.progressPercentage").value(33.33333333333333))
            .andExpect(jsonPath("$.data.completedTasks").value(1))
            .andExpect(jsonPath("$.data.totalTasks").value(3));
    }
    
    @Test
    @WithMockUser(authorities = "PLAN_VIEW")
    void testGetProgressSummary() throws Exception {
        mockMvc.perform(get("/api/v1/progress/summary")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.totalPlans").value(1))
            .andExpect(jsonPath("$.data.activePlans").value(1));
    }
    
    @Test
    @WithMockUser(authorities = "PLAN_VIEW")
    void testGetProgressHistory() throws Exception {
        mockMvc.perform(get("/api/v1/progress/history/{planId}", testPlanId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    @WithMockUser(authorities = "PLAN_VIEW")
    void testGetPlanProgressNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/progress/plan/{planId}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(authorities = "PLAN_CREATOR")
    void testGetProgressWithPlanCreatorRole() throws Exception {
        mockMvc.perform(get("/api/v1/progress/plan/{planId}", testPlanId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }
}
