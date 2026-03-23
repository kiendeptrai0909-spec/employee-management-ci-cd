package com.example.employee_management.controller;

import com.example.employee_management.entity.User;
import com.example.employee_management.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Bước 1:
 * - Chưa làm full CRUD
 * - Chỉ lấy danh sách users (có thể rỗng)
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

