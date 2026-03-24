package com.example.employee_management.exception;

import java.time.Instant;
import java.util.List;

public class ApiError {
    private String code;
    private int status;
    private String message;
    private Instant timestamp;
    private List<String> details;

    public ApiError() {
    }

    public ApiError(String code, int status, String message, List<String> details) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now();
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}

