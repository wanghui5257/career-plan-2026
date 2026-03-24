package com.career.plan.service;

import com.career.plan.entity.Progress;
import com.career.plan.repository.ProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProgressService {
    
    @Autowired
    private ProgressRepository progressRepository;
    
    public List<Progress> getAllProgress() {
        return progressRepository.findAll();
    }
    
    public List<Progress> getByPlanId(Long planId) {
        return progressRepository.findByPlanId(planId);
    }
    
    public List<Progress> getByTaskId(Long taskId) {
        return progressRepository.findByTaskId(taskId);
    }
    
    public Progress createProgress(Progress progress) {
        return progressRepository.save(progress);
    }
    
    public Progress updateProgress(Long id, Progress progress) {
        progress.setId(id);
        return progressRepository.save(progress);
    }
    
    public void deleteProgress(Long id) {
        progressRepository.deleteById(id);
    }
    
    public Map<String, Object> getProgressSummary() {
        List<Progress> allProgress = progressRepository.findAll();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", allProgress.size());
        summary.put("reports", allProgress);
        
        return summary;
    }
    
    /**
     * 获取所有计划的进度历史（用于图表展示）
     * @return 所有进度记录
     */
    public List<Progress> getAllProgressHistory() {
        return progressRepository.findAll();
    }
}
