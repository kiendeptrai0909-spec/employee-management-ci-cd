package com.example.employee_management.service;

import com.example.employee_management.dto.UserCreateRequest;
import com.example.employee_management.exception.DuplicateEmailException;
import com.example.employee_management.exception.ResourceNotFoundException;
import com.example.employee_management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_duplicateEmail_throws() {
        UserCreateRequest req = new UserCreateRequest();
        req.setName("Nguyen Van A");
        req.setEmail("dup@example.com");
        req.setPhone("0901234567");

        when(userRepository.existsByEmailAndDeletedAtIsNull(anyString())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.createUser(req));
    }

    @Test
    void getUserById_notFound_throws() {
        when(userRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }
}
