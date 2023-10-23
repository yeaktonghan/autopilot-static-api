package com.kshrd.autopilot.entities.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {
    @NotNull
    @NotBlank(message = "Please input username")
    private String username;
    private String password;
}
