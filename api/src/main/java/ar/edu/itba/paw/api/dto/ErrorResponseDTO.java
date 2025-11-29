package ar.edu.itba.paw.api.dto;


import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard DTO for API REST error responses.
 * Format: timestamp, code, message, path.
 */
public class ErrorResponseDTO {

    private LocalDateTime timestamp;
    private int code;
    private String status;
    private String message;
    private String path;
    private List<ValidationErrorDTO> validationErrors;

    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDTO(int code, String status, String message, String path) {
        this();
        this.code = code;
        this.status = status;
        this.message = message;
        this.path = path;
    }

    public ErrorResponseDTO(int code, String status, String message, String path, List<ValidationErrorDTO> validationErrors) {
        this(code, status, message, path);
        this.validationErrors = validationErrors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ValidationErrorDTO> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationErrorDTO> validationErrors) {
        this.validationErrors = validationErrors;
    }

    /**
     * DTO for individual validation errors
     */
    public static class ValidationErrorDTO {
        private String field;
        private String message;
        private Object rejectedValue;

        public ValidationErrorDTO() {
        }

        public ValidationErrorDTO(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public ValidationErrorDTO(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }
    }
}

