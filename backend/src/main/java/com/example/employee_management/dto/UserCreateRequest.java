package com.example.employee_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserCreateRequest {

    @NotBlank(message = "Tên nhân viên không được để trống")
    @Size(max = 200, message = "Tên nhân viên tối đa 200 ký tự")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 200, message = "Email tối đa 200 ký tự")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 30, message = "Số điện thoại tối đa 30 ký tự")
    // Cho phép số và một số ký tự phổ biến (+, -, khoảng trắng, dấu ngoặc)
    @Pattern(regexp = "^[0-9+\\-()\\s]{7,}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    public UserCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

