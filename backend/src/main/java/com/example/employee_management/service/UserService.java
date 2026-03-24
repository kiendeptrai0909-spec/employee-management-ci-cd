package com.example.employee_management.service;

import com.example.employee_management.dto.UserCreateRequest;
import com.example.employee_management.dto.UserPageResponse;
import com.example.employee_management.dto.UserResponse;
import com.example.employee_management.dto.UserUpdateRequest;
import com.example.employee_management.entity.User;
import com.example.employee_management.exception.DuplicateEmailException;
import com.example.employee_management.exception.ResourceNotFoundException;
import com.example.employee_management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserPageResponse getUsers(int page, int size, String sortBy, String sortDir, String keyword) {
        String sanitizedSortBy = sanitizeSortBy(sortBy);
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(direction, sanitizedSortBy));

        Specification<User> filterSpec = null;
        if (keyword != null && !keyword.trim().isEmpty()) {
            String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            filterSpec = (root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), normalizedKeyword),
                    cb.like(cb.lower(root.get("email")), normalizedKeyword),
                    cb.like(cb.lower(root.get("phone")), normalizedKeyword)
            );
        }

        Page<User> userPage = userRepository.findAll(filterSpec, pageable);
        List<UserResponse> items = userPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new UserPageResponse(
                items,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.hasNext(),
                userPage.hasPrevious()
        );
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

    private String sanitizeSortBy(String sortBy) {
        if ("name".equalsIgnoreCase(sortBy)) {
            return "name";
        }
        if ("email".equalsIgnoreCase(sortBy)) {
            return "email";
        }
        if ("phone".equalsIgnoreCase(sortBy)) {
            return "phone";
        }
        return "id";
    }
}

