package com.career.plan.repository;

import com.career.plan.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    
    /**
     * 根据计划 ID 查询进度
     */
    Optional<Progress> findByPlanId(Long planId);
    
    /**
     * 根据计划 ID 查询进度历史（按时间倒序）
     */
    List<Progress> findByPlanIdOrderByUpdatedAtDesc(Long planId);
    
    /**
     * 查询最新的进度记录
     */
    List<Progress> findTop10ByOrderByUpdatedAtDesc();
}
