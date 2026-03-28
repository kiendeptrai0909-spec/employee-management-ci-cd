package com.example.employee_management.security;

import com.example.employee_management.entity.AppAccount;
import com.example.employee_management.repository.AppAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppAccountRepository appAccountRepository;

    public CustomUserDetailsService(AppAccountRepository appAccountRepository) {
        this.appAccountRepository = appAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppAccount acc = appAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));
        return User.withUsername(acc.getUsername())
                .password(acc.getPassword())
                .roles(acc.getRole().name())
                .build();
    }
}
