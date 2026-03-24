package com.career.plan.config;

import com.career.plan.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.concurrent.TimeoutException;

/**
 * 全局异常处理器
 * 捕获所有未处理的异常，防止应用崩溃
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常（RuntimeException）
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("业务异常：{}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
    }

    /**
     * 处理认证异常（密码错误等）
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("认证失败：{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, "用户名或密码错误"));
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("参数验证失败：{}", e.getBindingResult().getFieldError());
        StringBuilder errorMsg = new StringBuilder("参数验证失败：");
        e.getBindingResult().getFieldErrors().forEach(error -> 
            errorMsg.append(error.getField()).append(" ").append(error.getDefaultMessage()).append("; ")
        );
        return ResponseEntity.badRequest().body(ApiResponse.error(400, errorMsg.toString()));
    }

    /**
     * 处理数据库约束 violation（唯一键冲突等）
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        log.error("数据库约束冲突：{}", e.getMessage());
        String message = e.getMessage();
        if (message.contains("Duplicate entry")) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "数据已存在"));
        } else if (message.contains("foreign key")) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "关联数据不存在"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(400, "数据库操作失败"));
    }

    /**
     * 处理 404 异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NoHandlerFoundException e) {
        log.warn("资源未找到：{}", e.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "资源未找到"));
    }

    /**
     * 处理超时异常
     */
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiResponse<Void>> handleTimeoutException(TimeoutException e) {
        log.error("请求超时：{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(ApiResponse.error(504, "请求超时，请稍后重试"));
    }

    /**
     * 处理所有其他未捕获的异常（兜底处理）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        log.error("未捕获的异常：{}", e.getMessage(), e);
        // 记录详细堆栈，但返回友好错误信息
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "服务器内部错误，请稍后重试"));
    }

    /**
     * 处理 Error（严重错误，如 OutOfMemoryError）
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResponse<Void>> handleThrowable(Throwable e) {
        log.error("严重错误：{}", e.getMessage(), e);
        // 记录详细堆栈，但返回友好错误信息
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "服务器严重错误，请联系管理员"));
    }
}
