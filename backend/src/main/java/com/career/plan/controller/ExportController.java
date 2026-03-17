package com.career.plan.controller;

import com.career.plan.dto.ExportDTO;
import com.career.plan.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/v1/export")
@CrossOrigin(origins = "*")
@Tag(name = "数据导出", description = "数据导出和下载 API")
public class ExportController {
    
    @Autowired
    private ExportService exportService;
    
    @PostMapping("/plan/{planId}")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "导出计划", description = "导出指定计划为 CSV/Excel/JSON 格式")
    public ResponseEntity<?> exportPlan(
            @PathVariable Long planId,
            @RequestParam(defaultValue = "JSON") String format) {
        try {
            ExportDTO.ExportResponse response = exportService.exportPlan(planId, format);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExportDTO.ExportResponse.class);
        }
    }
    
    @GetMapping("/{fileId}")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "下载导出文件", description = "下载指定的导出文件")
    public ResponseEntity<?> downloadExport(@PathVariable String fileId) {
        try {
            Path filePath = exportService.getExportFile(fileId);
            Resource resource = new FileSystemResource(filePath);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExportDTO.ExportResponse.class);
        }
    }
    
    @GetMapping("/history")
    @PreAuthorize("hasAnyAuthority('PLAN_VIEW', 'ADMIN')")
    @Operation(summary = "获取导出历史", description = "获取所有导出文件的历史记录")
    public ResponseEntity<?> getExportHistory() {
        try {
            List<ExportDTO.ExportHistoryItem> history = exportService.getExportHistory();
            return ResponseEntity.ok(ExportDTO.ExportResponse.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExportDTO.ExportResponse.class);
        }
    }
    
    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasAnyAuthority('PLAN_CREATE', 'ADMIN')")
    @Operation(summary = "删除导出文件", description = "删除指定的导出文件")
    public ResponseEntity<?> deleteExport(@PathVariable String fileId) {
        try {
            boolean deleted = exportService.deleteExportFile(fileId);
            return ResponseEntity.ok(ExportDTO.ExportResponse.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExportDTO.ExportResponse.class);
        }
    }
}
