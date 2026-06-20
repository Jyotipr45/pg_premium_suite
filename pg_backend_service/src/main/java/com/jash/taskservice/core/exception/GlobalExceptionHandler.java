package com.jash.taskservice.core.exception;

import com.jash.taskservice.domain.audit.DeveloperAuditLog;
import com.jash.taskservice.domain.audit.DeveloperAuditLogRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final DeveloperAuditLogRepository auditLogRepository;

    public GlobalExceptionHandler(DeveloperAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRuleException(BusinessRuleException ex) {
        logger.warn("⚠️ Business rule validation failed: {}", ex.getMessage());
        saveDeveloperAuditLog(HttpStatus.BAD_REQUEST.value(), ex);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Business Rule Violation");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("💥 Unhandled runtime system exception caught: ", ex);
        saveDeveloperAuditLog(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected system crash occurred. Developer diagnostics have been captured.");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void saveDeveloperAuditLog(int status, Exception ex) {
        try {
            DeveloperAuditLog auditLog = new DeveloperAuditLog();
            
            auditLog.setClientIp(MDC.get("clientIp"));
            auditLog.setRequestUrl(MDC.get("requestUrl"));
            auditLog.setHttpMethod(MDC.get("httpMethod"));
            auditLog.setHttpStatus(status);
            auditLog.setExceptionMessage(ex.getMessage());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            auditLog.setStackTrace(sw.toString());

            auditLogRepository.save(auditLog);
        } catch (Exception loggingError) {
            System.err.println("CRITICAL: Observability pipeline failed to commit developer log history: " + loggingError.getMessage());
        }
    }
}