package com.career.plan.scheduler;

import com.career.plan.service.ExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 导出定时任务
 * 每天凌晨 2 点清理过期的导出文件
 */
@Component
public class ExportScheduler {
    
    private static final Logger log = LoggerFactory.getLogger(ExportScheduler.class);
    
    @Autowired
    private ExportService exportService;
    
    /**
     * 每天凌晨 2 点清理过期导出文件
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredExports() {
        log.info("开始清理过期导出文件...");
        exportService.cleanupExpiredExports();
        log.info("清理完成，删除 {} 个过期文件", 0);
    }
}
