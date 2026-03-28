package com.example.employee_management.repository;

import com.example.employee_management.entity.AppAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppAccountRepository extends JpaRepository<AppAccount, Long> {
    Optional<AppAccount> findByUsername(String username);
}
