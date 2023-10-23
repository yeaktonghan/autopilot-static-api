package com.kshrd.autopilot;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(
        name = "auth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer"
)
public class AutopilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutopilotApplication.class, args);
    }

}
