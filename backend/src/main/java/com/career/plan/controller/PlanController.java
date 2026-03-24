package com.career.plan.controller;

import com.career.plan.entity.Plan;
import com.career.plan.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plans")
@CrossOrigin(origins = "*")
public class PlanController {
    
    @Autowired
    private PlanRepository planRepository;
    
    @GetMapping
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Plan getPlan(@PathVariable Long id) {
        return planRepository.findById(id).orElse(null);
    }
    
    @PostMapping
    public Plan createPlan(@RequestBody Plan plan) {
        return planRepository.save(plan);
    }
    
    @PutMapping("/{id}")
    public Plan updatePlan(@PathVariable Long id, @RequestBody Plan plan) {
        plan.setId(id);
        return planRepository.save(plan);
    }
    
    @DeleteMapping("/{id}")
    public void deletePlan(@PathVariable Long id) {
        planRepository.deleteById(id);
    }
    
    @GetMapping("/assignee/{assignee}")
    public List<Plan> getByAssignee(@PathVariable String assignee) {
        return planRepository.findByAssignedTo(assignee);
    }
    
    @GetMapping("/user/{userId}")
    public List<Plan> getByUser(@PathVariable Long userId) {
        return planRepository.findByUserId(userId);
    }
}
