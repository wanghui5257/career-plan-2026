package com.career.plan.service;

import com.career.plan.dto.ExportDTO;
import com.career.plan.entity.Plan;
import com.career.plan.entity.Task;
import com.career.plan.repository.PlanRepository;
import com.career.plan.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExportService {
    
    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    private static final String EXPORT_DIR = "/tmp/career-plan-exports";
    private static final long EXPIRY_HOURS = 168; // 7 天
    
    /**
     * 导出计划为指定格式
     */
    public ExportDTO.ExportResponse exportPlan(Long planId, String format) throws IOException {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在：" + planId));
        
        List<Task> tasks = taskRepository.findAll();
        
        // 生成导出数据
        ExportDTO.PlanExportData data = new ExportDTO.PlanExportData();
        data.setPlanId(plan.getId());
        data.setPlanName(plan.getTitle());
        data.setDescription(plan.getDescription());
        data.setProgress(0);
        data.setCreatedAt(plan.getCreatedAt());
        
        List<ExportDTO.TaskExportData> taskDataList = tasks.stream().map(task -> {
            ExportDTO.TaskExportData taskData = new ExportDTO.TaskExportData();
            taskData.setTaskId(task.getId());
            taskData.setTitle(task.getTitle());
            taskData.setDescription(task.getDescription());
            taskData.setStatus(task.getStatus());
            taskData.setPriority(task.getPriority());
            taskData.setProgress(task.getProgress());
            taskData.setAssignee(task.getAssignedTo());
            taskData.setDueDate(task.getDueDate());
            return taskData;
        }).collect(Collectors.toList());
        data.setTasks(taskDataList);
        
        // 生成文件
        String fileId = UUID.randomUUID().toString();
        String fileName = "plan_" + planId + "_" + System.currentTimeMillis() + "." + format.toLowerCase();
        Path filePath = Paths.get(EXPORT_DIR, fileName);
        
        // 确保导出目录存在
        Files.createDirectories(Paths.get(EXPORT_DIR));
        
        // 根据格式写入文件
        switch (format.toUpperCase()) {
            case "CSV":
                writeCsv(data, filePath);
                break;
            case "EXCEL":
                writeExcel(data, filePath);
                break;
            case "JSON":
                writeJson(data, filePath);
                break;
            default:
                writeJson(data, filePath); // 默认 JSON
        }
        
        // 生成响应
        ExportDTO.ExportResponse response = new ExportDTO.ExportResponse();
        response.setFileId(fileId);
        response.setFileName(fileName);
        response.setFormat(format.toUpperCase());
        response.setFileSize(Files.size(filePath));
        response.setDownloadUrl("/api/v1/export/" + fileId);
        response.setGeneratedAt(LocalDateTime.now());
        response.setExpiresAt(LocalDateTime.now().plusHours(EXPIRY_HOURS));
        
        return response;
    }
    
    /**
     * 写入 CSV 文件
     */
    private void writeCsv(ExportDTO.PlanExportData data, Path filePath) throws IOException {
        StringBuilder csv = new StringBuilder();
        
        // 计划信息
        csv.append("计划 ID, 计划名称，描述，创建时间\n");
        csv.append(data.getPlanId()).append(",")
           .append(data.getPlanName()).append(",")
           .append(data.getDescription()).append(",")
           .append(data.getCreatedAt()).append("\n\n");
        
        // 任务列表
        csv.append("任务 ID，任务标题，描述，状态，优先级，进度，负责人，截止日期\n");
        for (ExportDTO.TaskExportData task : data.getTasks()) {
            csv.append(task.getTaskId()).append(",")
               .append(task.getTitle()).append(",")
               .append(task.getDescription()).append(",")
               .append(task.getStatus()).append(",")
               .append(task.getPriority()).append(",")
               .append(task.getProgress()).append(",")
               .append(task.getAssignee()).append(",")
               .append(task.getDueDate()).append("\n");
        }
        
        Files.write(filePath, csv.toString().getBytes());
    }
    
    /**
     * 写入 Excel 文件（简化版，实际应使用 Apache POI）
     */
    private void writeExcel(ExportDTO.PlanExportData data, Path filePath) throws IOException {
        // 简化实现：生成 CSV 但使用.xlsx 扩展名
        // 生产环境应使用 Apache POI 库
        writeCsv(data, filePath);
    }
    
    /**
     * 写入 JSON 文件
     */
    private void writeJson(ExportDTO.PlanExportData data, Path filePath) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"planId\": ").append(data.getPlanId()).append(",\n");
        json.append("  \"planName\": \"").append(data.getPlanName()).append("\",\n");
        json.append("  \"description\": \"").append(data.getDescription()).append("\",\n");
        json.append("  \"createdAt\": \"").append(data.getCreatedAt()).append("\",\n");
        json.append("  \"tasks\": [\n");
        
        for (int i = 0; i < data.getTasks().size(); i++) {
            ExportDTO.TaskExportData task = data.getTasks().get(i);
            json.append("    {\n");
            json.append("      \"taskId\": ").append(task.getTaskId()).append(",\n");
            json.append("      \"title\": \"").append(task.getTitle()).append("\",\n");
            json.append("      \"status\": \"").append(task.getStatus()).append("\",\n");
            json.append("      \"priority\": \"").append(task.getPriority()).append("\",\n");
            json.append("      \"progress\": ").append(task.getProgress()).append("\n");
            json.append("    }");
            if (i < data.getTasks().size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("  ]\n");
        json.append("}\n");
        
        Files.write(filePath, json.toString().getBytes());
    }
    
    /**
     * 获取导出文件
     */
    public Path getExportFile(String fileId) throws IOException {
        // 简单实现：查找匹配的文件
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(EXPORT_DIR))) {
            for (Path file : stream) {
                if (file.getFileName().toString().contains(fileId)) {
                    return file;
                }
            }
        }
        throw new FileNotFoundException("导出文件不存在：" + fileId);
    }
    
    /**
     * 获取导出历史
     */
    @Transactional(readOnly = true)
    public List<ExportDTO.ExportHistoryItem> getExportHistory() {
        List<ExportDTO.ExportHistoryItem> history = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(EXPORT_DIR))) {
            for (Path file : stream) {
                try {
                    ExportDTO.ExportHistoryItem item = new ExportDTO.ExportHistoryItem();
                    item.setFileId(file.getFileName().toString());
                    item.setFileName(file.getFileName().toString());
                    item.setFormat(file.getFileName().toString().substring(file.getFileName().toString().lastIndexOf('.') + 1).toUpperCase());
                    item.setFileSize(Files.size(file));
                    item.setGeneratedAt(LocalDateTime.ofInstant(Files.getLastModifiedTime(file).toInstant(), java.time.ZoneId.systemDefault()));
                    item.setExpiresAt(item.getGeneratedAt().plusHours(EXPIRY_HOURS));
                    item.setStatus(now.isAfter(item.getExpiresAt()) ? "EXPIRED" : "READY");
                    history.add(item);
                } catch (Exception e) {
                    // 忽略错误
                }
            }
        } catch (IOException e) {
            // 忽略错误
        }
        
        return history;
    }
    
    /**
     * 删除导出文件
     */
    public boolean deleteExportFile(String fileId) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(EXPORT_DIR))) {
            for (Path file : stream) {
                if (file.getFileName().toString().contains(fileId)) {
                    Files.delete(file);
                    return true;
                }
            }
        } catch (IOException e) {
            // 忽略错误
        }
        return false;
    }
    
    /**
     * 定时清理：每天清理过期的导出文件
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨 2 点执行
    public void cleanupExpiredExports() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = 0;
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(EXPORT_DIR))) {
            for (Path file : stream) {
                try {
                    LocalDateTime generatedAt = LocalDateTime.ofInstant(
                        Files.getLastModifiedTime(file).toInstant(), 
                        java.time.ZoneId.systemDefault()
                    );
                    
                    if (now.isAfter(generatedAt.plusHours(EXPIRY_HOURS))) {
                        Files.delete(file);
                        deletedCount++;
                    }
                } catch (Exception e) {
                    // 忽略错误
                }
            }
        } catch (IOException e) {
            // 忽略错误
        }
        
        System.out.println("清理导出文件完成，删除 " + deletedCount + " 个过期文件");
    }
}
