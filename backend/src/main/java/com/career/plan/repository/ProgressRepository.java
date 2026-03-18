package com.career.plan.repository;

import com.career.plan.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByPlanId(Long planId);
    List<Progress> findByTaskId(Long taskId);
}
