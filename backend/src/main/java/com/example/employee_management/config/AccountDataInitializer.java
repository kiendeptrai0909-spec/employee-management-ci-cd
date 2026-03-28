package com.example.employee_management.config;

import com.example.employee_management.entity.AppAccount;
import com.example.employee_management.entity.Role;
import com.example.employee_management.repository.AppAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AccountDataInitializer {

    @Bean
    CommandLineRunner seedAccounts(
            AppAccountRepository repo,
            PasswordEncoder encoder,
            @Value("${app.seed.enabled:true}") boolean seedEnabled,
            @Value("${app.seed.admin-username:admin}") String adminUser,
            @Value("${app.seed.admin-password:Admin@123}") String adminPass,
            @Value("${app.seed.user-username:user}") String normalUser,
            @Value("${app.seed.user-password:User@123}") String userPass
    ) {
        return args -> {
            if (!seedEnabled) {
                return;
            }
            if (repo.findByUsername(adminUser).isEmpty()) {
                repo.save(new AppAccount(adminUser, encoder.encode(adminPass), Role.ADMIN));
            }
            if (repo.findByUsername(normalUser).isEmpty()) {
                repo.save(new AppAccount(normalUser, encoder.encode(userPass), Role.USER));
            }
        };
    }
}
