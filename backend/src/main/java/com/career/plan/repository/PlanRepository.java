package com.career.plan.repository;

import com.career.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    /**
     * 根据用户 ID 查询计划列表
     */
    List<Plan> findByUserId(Long userId);
    
    /**
     * 根据租户 ID 查询计划列表
     */
    List<Plan> findByTenantId(String tenantId);
    
    /**
     * 根据用户 ID 和租户 ID 查询计划列表
     */
    List<Plan> findByUserIdAndTenantId(Long userId, String tenantId);
    
    /**
     * 根据状态查询计划列表
     */
    List<Plan> findByStatus(String status);
    
    /**
     * 根据用户 ID 和状态查询计划列表
     */
    List<Plan> findByUserIdAndStatus(Long userId, String status);
}
