package com.example.employee_management.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class BulkDeleteRequest {

    @NotEmpty(message = "ids không được rỗng")
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
