package com.example.employee_management.controller;

import com.example.employee_management.dto.UserCreateRequest;
import com.example.employee_management.dto.UserResponse;
import com.example.employee_management.dto.UserUpdateRequest;
import com.example.employee_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse updated = userService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

