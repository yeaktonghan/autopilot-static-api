package com.kshrd.autopilot.entities.request;

import com.kshrd.autopilot.entities.user.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthenticationRequest {
    @NotNull(message = "Please enter username.")
    private String username;
    private String fullName;
    @Email(message = "Please enter a valid email address.")
    private String email;
    @NotNull
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
            message = "Password must contain 8 to 20 characters at least one digit, lower, upper case and one special character."
    )
    private String password;
    private Gender gender;
}
