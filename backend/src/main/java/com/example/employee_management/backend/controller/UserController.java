package com.example.employee_management.controller;

import com.example.employee_management.dto.ApiResponse;
import com.example.employee_management.dto.UserCreateRequest;
import com.example.employee_management.dto.UserPageResponse;
import com.example.employee_management.dto.UserResponse;
import com.example.employee_management.dto.UserUpdateRequest;
import com.example.employee_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CRUD cho bảng users:
 * - GET /api/users
 * - GET /api/users/{id}
 * - POST /api/users
 * - PUT /api/users/{id}
 * - DELETE /api/users/{id}
 *
 * Có comment tiếng Việt ở logic validate thông qua DTO.
 */
@RestController
@CrossOrigin(origins = "*") // Dùng cho demo (khi deploy thật có thể siết origin)
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<UserPageResponse> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword
    ) {
        UserPageResponse result = userService.getUsers(page, size, sortBy, sortDir, keyword);
        return new ApiResponse<>("Lấy danh sách nhân viên thành công", result);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return new ApiResponse<>("Lấy thông tin nhân viên thành công", user);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Tạo nhân viên thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse updated = userService.updateUser(id, request);
        return ResponseEntity.ok(new ApiResponse<>("Cập nhật nhân viên thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>("Xóa nhân viên thành công", null));
    }
}

