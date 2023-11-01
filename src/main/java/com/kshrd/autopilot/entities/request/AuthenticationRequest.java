package com.kshrd.autopilot.entities.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthenticationRequest {
    @NotNull(message = "Please enter username.")
    private String username;
    @Email(message = "Please enter a valid email address.")
    @NotNull
    private String email;
    @NotNull
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
            message = "Password must contain 8 characters at least one digit,special character."
    )
    private String password;
}
