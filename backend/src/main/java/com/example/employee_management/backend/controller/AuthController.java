package com.example.employee_management.controller;

import com.example.employee_management.dto.ApiResponse;
import com.example.employee_management.dto.LoginRequest;
import com.example.employee_management.dto.LoginResponse;
import com.example.employee_management.entity.AppAccount;
import com.example.employee_management.entity.Role;
import com.example.employee_management.repository.AppAccountRepository;
import com.example.employee_management.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppAccountRepository appAccountRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AppAccountRepository appAccountRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.appAccountRepository = appAccountRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            UserDetails principal = (UserDetails) auth.getPrincipal();
            AppAccount account = appAccountRepository.findByUsername(principal.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Tài khoản không hợp lệ"));
            Role role = account.getRole();
            String token = jwtService.generateToken(account.getUsername(), role);
            LoginResponse body = new LoginResponse("Bearer", token, account.getUsername(), role);
            return ResponseEntity.ok(new ApiResponse<>("Đăng nhập thành công", body));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("Sai tên đăng nhập hoặc mật khẩu", null));
        }
    }
}
