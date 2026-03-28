package com.example.employee_management.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.example.employee_management.dto.ApiResponse;
import com.example.employee_management.dto.BulkDeleteRequest;
import com.example.employee_management.dto.CsvImportResult;
import com.example.employee_management.dto.UserCreateRequest;
import com.example.employee_management.dto.UserPageResponse;
import com.example.employee_management.dto.UserResponse;
import com.example.employee_management.dto.UserUpdateRequest;
import com.example.employee_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportUsersCsv() throws IOException {
        byte[] body = userService.exportUsersCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(body);
    }

    @PostMapping(value = "/import/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CsvImportResult>> importUsersCsv(@RequestPart("file") MultipartFile file)
            throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("File rỗng", null));
        }
        CsvImportResult result = userService.importUsersFromCsv(file.getInputStream());
        return ResponseEntity.ok(new ApiResponse<>("Import CSV hoàn tất", result));
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<ApiResponse<Void>> bulkDelete(@Valid @RequestBody BulkDeleteRequest request) {
        userService.bulkSoftDelete(request);
        return ResponseEntity.ok(new ApiResponse<>("Đã xóa mềm các bản ghi được chọn", null));
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

