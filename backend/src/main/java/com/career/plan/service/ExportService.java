package com.career.plan.service;

import com.career.plan.dto.ExportDTO;
import com.career.plan.entity.Plan;
import com.career.plan.entity.Task;
import com.career.plan.repository.PlanRepository;
import com.career.plan.repository.TaskRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExportService {
    
    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    public static final String EXPORT_DIR = "/tmp/career-plan-exports";
    private static final long EXPIRY_HOURS = 168;  // 7 days
    
    /**
     * 导出计划为指定格式
     */
    @Transactional(readOnly = true)
    public ExportDTO.ExportResponse exportPlan(Long planId, String format) throws IOException {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在"));
        
        List<Task> tasks = taskRepository.findByPlanId(planId);
        
        // 创建导出目录
        File exportDir = new File(EXPORT_DIR);
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        
        // 生成文件名
        String fileId = UUID.randomUUID().toString().substring(0, 8);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = String.format("plan_%d_%s.%s", planId, timestamp, format.toLowerCase());
        String filePath = EXPORT_DIR + "/" + fileName;
        
        // 根据格式导出
        switch (format.toUpperCase()) {
            case "CSV":
                exportToCsv(plan, tasks, filePath);
                break;
            case "EXCEL":
                exportToExcel(plan, tasks, filePath);
                break;
            case "JSON":
                exportToJson(plan, tasks, filePath);
                break;
            default:
                throw new IllegalArgumentException("不支持的导出格式：" + format);
        }
        
        LocalDateTime generatedAt = LocalDateTime.now();
        LocalDateTime expiresAt = generatedAt.plusHours(EXPIRY_HOURS);
        
        return new ExportDTO.ExportResponse(
            fileId,
            fileName,
            format.toUpperCase(),
            planId,
            plan.getTitle(),
            generatedAt,
            expiresAt,
            "/api/v1/export/" + fileId
        );
    }
    
    /**
     * 导出为 CSV
     */
    private void exportToCsv(Plan plan, List<Task> tasks, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // 计划信息
            writer.append("=== 计划信息 ===\n");
            writer.append("计划 ID,").append(plan.getId().toString()).append("\n");
            writer.append("计划名称,").append(plan.getTitle()).append("\n");
            writer.append("计划描述,").append(plan.getDescription() != null ? plan.getDescription() : "").append("\n");
            writer.append("开始日期,").append(plan.getStartDate() != null ? plan.getStartDate().toString() : "").append("\n");
            writer.append("结束日期,").append(plan.getEndDate() != null ? plan.getEndDate().toString() : "").append("\n");
            writer.append("\n");
            
            // 任务列表
            writer.append("=== 任务列表 ===\n");
            writer.append("任务 ID，标题，描述，状态，优先级，截止日期\n");
            for (Task task : tasks) {
                writer.append(task.getId().toString()).append(",");
                writer.append(task.getTitle()).append(",");
                writer.append(task.getDescription() != null ? task.getDescription() : "").append(",");
                writer.append(task.getStatus() != null ? task.getStatus() : "").append(",");
                writer.append(task.getPriority() != null ? task.getPriority() : "").append(",");
                writer.append(task.getDueDate() != null ? task.getDueDate().toString() : "").append("\n");
            }
        }
    }
    
    /**
     * 导出为 Excel
     */
    private void exportToExcel(Plan plan, List<Task> tasks, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 计划信息 Sheet
            Sheet planSheet = workbook.createSheet("计划信息");
            Row headerRow = planSheet.createRow(0);
            headerRow.createCell(0).setCellValue("属性");
            headerRow.createCell(1).setCellValue("值");
            
            int rowNum = 1;
            planSheet.createRow(rowNum++).createCell(0).setCellValue("计划 ID");
            planSheet.getRow(rowNum - 1).createCell(1).setCellValue(plan.getId());
            
            planSheet.createRow(rowNum++).createCell(0).setCellValue("计划名称");
            planSheet.getRow(rowNum - 1).createCell(1).setCellValue(plan.getTitle());
            
            if (plan.getDescription() != null) {
                planSheet.createRow(rowNum++).createCell(0).setCellValue("计划描述");
                planSheet.getRow(rowNum - 1).createCell(1).setCellValue(plan.getDescription());
            }
            
            if (plan.getStartDate() != null) {
                planSheet.createRow(rowNum++).createCell(0).setCellValue("开始日期");
                planSheet.getRow(rowNum - 1).createCell(1).setCellValue(plan.getStartDate().toString());
            }
            
            if (plan.getEndDate() != null) {
                planSheet.createRow(rowNum++).createCell(0).setCellValue("结束日期");
                planSheet.getRow(rowNum - 1).createCell(1).setCellValue(plan.getEndDate().toString());
            }
            
            // 任务列表 Sheet
            Sheet taskSheet = workbook.createSheet("任务列表");
            Row taskHeader = taskSheet.createRow(0);
            taskHeader.createCell(0).setCellValue("任务 ID");
            taskHeader.createCell(1).setCellValue("标题");
            taskHeader.createCell(2).setCellValue("描述");
            taskHeader.createCell(3).setCellValue("状态");
            taskHeader.createCell(4).setCellValue("优先级");
            taskHeader.createCell(5).setCellValue("截止日期");
            
            int taskRowNum = 1;
            for (Task task : tasks) {
                Row row = taskSheet.createRow(taskRowNum++);
                row.createCell(0).setCellValue(task.getId());
                row.createCell(1).setCellValue(task.getTitle());
                row.createCell(2).setCellValue(task.getDescription() != null ? task.getDescription() : "");
                row.createCell(3).setCellValue(task.getStatus() != null ? task.getStatus() : "");
                row.createCell(4).setCellValue(task.getPriority() != null ? task.getPriority() : "");
                row.createCell(5).setCellValue(task.getDueDate() != null ? task.getDueDate().toString() : "");
            }
            
            // 自动调整列宽
            for (int i = 0; i < 6; i++) {
                taskSheet.autoSizeColumn(i);
            }
            
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }
    
    /**
     * 导出为 JSON
     */
    private void exportToJson(Plan plan, List<Task> tasks, String filePath) throws IOException {
        List<ExportDTO.TaskData> taskDataList = tasks.stream()
            .map(task -> new ExportDTO.TaskData(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate()
            ))
            .collect(Collectors.toList());
        
        int completedTasks = (int) tasks.stream()
            .filter(task -> "DONE".equals(task.getStatus()) || "COMPLETED".equals(task.getStatus()))
            .count();
        
        ExportDTO.ProgressData progressData = new ExportDTO.ProgressData(
            tasks.size() > 0 ? (double) completedTasks / tasks.size() * 100 : 0.0,
            completedTasks,
            tasks.size()
        );
        
        ExportDTO.ExportData exportData = new ExportDTO.ExportData(
            plan.getId(),
            plan.getTitle(),
            plan.getDescription(),
            plan.getStartDate(),
            plan.getEndDate(),
            taskDataList,
            progressData
        );
        
        // 简单的 JSON 序列化（实际项目中建议使用 Jackson）
        String json = "{\n" +
            "  \"planId\": " + plan.getId() + ",\n" +
            "  \"planName\": \"" + escapeJson(plan.getTitle()) + "\",\n" +
            "  \"planDescription\": \"" + escapeJson(plan.getDescription() != null ? plan.getDescription() : "") + "\",\n" +
            "  \"startDate\": \"" + (plan.getStartDate() != null ? plan.getStartDate().toString() : "") + "\",\n" +
            "  \"endDate\": \"" + (plan.getEndDate() != null ? plan.getEndDate().toString() : "") + "\",\n" +
            "  \"tasks\": " + taskDataListToJson(taskDataList) + ",\n" +
            "  \"progress\": {\n" +
            "    \"progressPercentage\": " + progressData.getProgressPercentage() + ",\n" +
            "    \"completedTasks\": " + progressData.getCompletedTasks() + ",\n" +
            "    \"totalTasks\": " + progressData.getTotalTasks() + "\n" +
            "  }\n" +
            "}";
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
        }
    }
    
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
    
    private String taskDataListToJson(List<ExportDTO.TaskData> tasks) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < tasks.size(); i++) {
            ExportDTO.TaskData task = tasks.get(i);
            sb.append("    {\n");
            sb.append("      \"taskId\": ").append(task.getTaskId()).append(",\n");
            sb.append("      \"title\": \"").append(escapeJson(task.getTitle())).append("\",\n");
            sb.append("      \"description\": \"").append(escapeJson(task.getDescription() != null ? task.getDescription() : "")).append("\",\n");
            sb.append("      \"status\": \"").append(escapeJson(task.getStatus() != null ? task.getStatus() : "")).append("\",\n");
            sb.append("      \"priority\": \"").append(escapeJson(task.getPriority() != null ? task.getPriority() : "")).append("\",\n");
            sb.append("      \"dueDate\": \"").append(task.getDueDate() != null ? task.getDueDate().toString() : "").append("\"\n");
            sb.append("    }");
            if (i < tasks.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]");
        return sb.toString();
    }
    
    /**
     * 获取导出文件
     */
    public File getExportFile(String fileId) {
        File exportDir = new File(EXPORT_DIR);
        if (!exportDir.exists()) {
            return null;
        }
        
        // 查找匹配的文件
        File[] files = exportDir.listFiles((dir, name) -> name.contains(fileId));
        if (files != null && files.length > 0) {
            return files[0];
        }
        return null;
    }
    
    /**
     * 删除导出文件
     */
    public boolean deleteExportFile(String fileId) {
        File file = getExportFile(fileId);
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }
    
    /**
     * 清理过期导出文件
     */
    @Transactional
    public int cleanupExpiredExports() {
        File exportDir = new File(EXPORT_DIR);
        if (!exportDir.exists()) {
            return 0;
        }
        
        int deletedCount = 0;
        LocalDateTime now = LocalDateTime.now();
        File[] files = exportDir.listFiles();
        
        if (files != null) {
            for (File file : files) {
                // 简单判断：文件修改时间超过 7 天
                long fileAge = now.toEpochSecond() - file.lastModified() / 1000;
                if (fileAge > EXPIRY_HOURS * 3600) {
                    if (file.delete()) {
                        deletedCount++;
                    }
                }
            }
        }
        
        return deletedCount;
    }
}
