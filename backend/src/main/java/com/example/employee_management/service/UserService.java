package com.example.employee_management.service;

import com.example.employee_management.dto.BulkDeleteRequest;
import com.example.employee_management.dto.CsvImportResult;
import com.example.employee_management.dto.UserCreateRequest;
import com.example.employee_management.dto.UserPageResponse;
import com.example.employee_management.dto.UserResponse;
import com.example.employee_management.dto.UserUpdateRequest;
import com.example.employee_management.entity.User;
import com.example.employee_management.exception.DuplicateEmailException;
import com.example.employee_management.exception.ResourceNotFoundException;
import com.example.employee_management.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static Specification<User> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public UserPageResponse getUsers(int page, int size, String sortBy, String sortDir, String keyword) {
        String sanitizedSortBy = sanitizeSortBy(sortBy);
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(direction, sanitizedSortBy));

        Specification<User> filterSpec = notDeleted();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            Specification<User> keywordSpec = (root, query, cb) -> cb.and(
                    cb.isNull(root.get("deletedAt")),
                    cb.or(
                            cb.like(cb.lower(root.get("name")), normalizedKeyword),
                            cb.like(cb.lower(root.get("email")), normalizedKeyword),
                            cb.like(cb.lower(root.get("phone")), normalizedKeyword)
                    )
            );
            filterSpec = keywordSpec;
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
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id=" + id));
        return toResponse(user);
    }

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new DuplicateEmailException("Email đã tồn tại: " + request.getEmail());
        }
        User user = new User(request.getName(), request.getEmail(), request.getPhone());
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User existing = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id=" + id));

        if (!existing.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new DuplicateEmailException("Email đã tồn tại: " + request.getEmail());
        }

        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());

        User saved = userRepository.save(existing);
        return toResponse(saved);
    }

    public void deleteUser(Long id) {
        User existing = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id=" + id));
        existing.setDeletedAt(Instant.now());
        userRepository.save(existing);
    }

    @Transactional
    public void bulkSoftDelete(BulkDeleteRequest request) {
        for (Long id : request.getIds()) {
            userRepository.findByIdAndDeletedAtIsNull(id).ifPresent(u -> {
                u.setDeletedAt(Instant.now());
                userRepository.save(u);
            });
        }
    }

    public byte[] exportUsersCsv() throws IOException {
        List<User> users = userRepository.findAll(notDeleted());
        StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("id", "name", "email", "phone", "createdAt", "updatedAt")
                .build();
        try (CSVPrinter printer = new CSVPrinter(sw, format)) {
            for (User u : users) {
                printer.printRecord(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getCreatedAt() != null ? u.getCreatedAt().toString() : "",
                        u.getUpdatedAt() != null ? u.getUpdatedAt().toString() : ""
                );
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(0xEF);
        out.write(0xBB);
        out.write(0xBF);
        out.write(sw.toString().getBytes(StandardCharsets.UTF_8));
        return out.toByteArray();
    }

    @Transactional
    public CsvImportResult importUsersFromCsv(InputStream input) throws IOException {
        int imported = 0;
        int skipped = 0;
        try (CSVParser parser = CSVParser.parse(
                input,
                StandardCharsets.UTF_8,
                CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build()
        )) {
            for (CSVRecord record : parser) {
                String name = trimToNull(record.get("name"));
                String email = trimToNull(record.get("email"));
                String phone = trimToNull(record.get("phone"));
                if (name == null || email == null || phone == null) {
                    skipped++;
                    continue;
                }
                if (userRepository.existsByEmailAndDeletedAtIsNull(email)) {
                    skipped++;
                    continue;
                }
                userRepository.save(new User(name, email, phone));
                imported++;
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("File CSV cần có cột: name, email, phone", e);
        }
        return new CsvImportResult(imported, skipped);
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
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
        if ("createdAt".equalsIgnoreCase(sortBy)) {
            return "createdAt";
        }
        if ("updatedAt".equalsIgnoreCase(sortBy)) {
            return "updatedAt";
        }
        return "id";
    }
}
