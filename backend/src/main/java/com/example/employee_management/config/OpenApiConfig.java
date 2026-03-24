package com.example.employee_management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeeManagementOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee Management API")
                        .description("API quản lý nhân viên với CRUD, tìm kiếm, sắp xếp và phân trang")
                        .version("v1")
                        .contact(new Contact().name("Employee Management Team")));
    }
}
