package com.jash.taskservice.config;

import com.jash.taskservice.domain.exception.BusinessRuleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessRule(BusinessRuleException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage(), "BED_SNATCHED");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("System went on a coffee break.", "SERVER_COFFEE_BREAK");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    public static class ErrorResponseDTO {
        private String message;
        private String code;

        public ErrorResponseDTO(String message, String code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage() { return message; }
        public String getCode() { return code; }
    }
}
