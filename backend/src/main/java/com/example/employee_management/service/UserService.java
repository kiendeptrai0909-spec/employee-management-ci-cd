package com.example.employee_management.service;

import com.example.employee_management.dto.UserCreateRequest;
import com.example.employee_management.dto.UserResponse;
import com.example.employee_management.dto.UserUpdateRequest;
import com.example.employee_management.entity.User;
import com.example.employee_management.exception.DuplicateEmailException;
import com.example.employee_management.exception.ResourceNotFoundException;
import com.example.employee_management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id=" + id));
        return toResponse(user);
    }

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email đã tồn tại: " + request.getEmail());
        }
        User user = new User(request.getName(), request.getEmail(), request.getPhone());
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id=" + id));

        // Nếu đổi email thì phải kiểm tra trùng
        if (!existing.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email đã tồn tại: " + request.getEmail());
        }

        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());

        User saved = userRepository.save(existing);
        return toResponse(saved);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy user với id=" + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone());
    }
}

