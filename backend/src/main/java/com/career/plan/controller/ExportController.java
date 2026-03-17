package com.career.plan.controller;

import com.career.plan.dto.ExportDTO;
import com.career.plan.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/export")
@CrossOrigin(origins = "*")
@Tag(name = "数据导出", description = "计划数据导出 API（CSV/Excel/JSON）")
public class ExportController {
    
    @Autowired
    private ExportService exportService;
    
    @PostMapping("/plan/{planId}")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "导出计划", description = "导出指定计划为 CSV/Excel/JSON 格式")
    public ResponseEntity<?> exportPlan(@PathVariable Long planId,
                                        @RequestParam(defaultValue = "EXCEL") String format) {
        try {
            ExportDTO.ExportResponse response = exportService.exportPlan(planId, format);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "导出成功",
                "data", response
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "不支持的导出格式：" + format
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "导出失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/{fileId}")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "下载导出文件", description = "下载已生成的导出文件")
    public ResponseEntity<?> downloadExport(@PathVariable String fileId) {
        try {
            File file = exportService.getExportFile(fileId);
            if (file == null || !file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            FileSystemResource resource = new FileSystemResource(file);
            String contentType = getContentType(file.getName());
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "下载失败：" + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/history")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取导出历史", description = "获取当前用户的导出历史记录")
    public ResponseEntity<?> getExportHistory() {
        try {
            // 简单实现：列出导出目录中的所有文件
            File exportDir = new File(ExportService.EXPORT_DIR);
            if (!exportDir.exists()) {
                return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "获取成功",
                    "data", List.of()
                ));
            }
            
            File[] files = exportDir.listFiles();
            List<ExportDTO.ExportHistoryItem> history = List.of();  // 简化实现
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", history
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "获取历史失败：" + e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "删除导出文件", description = "删除指定的导出文件")
    public ResponseEntity<?> deleteExport(@PathVariable String fileId) {
        try {
            boolean deleted = exportService.deleteExportFile(fileId);
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "删除成功"
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "删除失败：" + e.getMessage()
            ));
        }
    }
    
    private String getContentType(String fileName) {
        if (fileName.endsWith(".csv")) {
            return "text/csv";
        } else if (fileName.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (fileName.endsWith(".json")) {
            return "application/json";
        }
        return "application/octet-stream";
    }
}
