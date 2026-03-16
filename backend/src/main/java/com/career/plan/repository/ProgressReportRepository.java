package com.career.plan.repository;

import com.career.plan.entity.ProgressReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProgressReportRepository extends JpaRepository<ProgressReport, Long> {
    List<ProgressReport> findByTaskId(Long taskId);
    List<ProgressReport> findByUserId(Long userId);
}
