package com.career.plan.repository;

import com.career.plan.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    
    /**
     * 根据计划 ID 查找最新进度
     */
    Optional<Progress> findTopByPlanIdOrderByUpdatedAtDesc(@Param("planId") Long planId);
    
    /**
     * 根据计划 ID 查找所有进度历史
     */
    List<Progress> findByPlanIdOrderByUpdatedAtDesc(@Param("planId") Long planId);
    
    /**
     * 根据计划 ID 列表查找进度
     */
    List<Progress> findByPlanIdIn(@Param("planIds") List<Long> planIds);
}
