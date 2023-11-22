package com.kshrd.autopilot.entities.request;

import com.kshrd.autopilot.entities.user.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NotNull(message = "Please enter fullname")
    @NotBlank(message = "Please enter fullname")
    private String fullName;
    @NotNull(message = "Please enter username")
    @NotBlank(message = "Please enter username")
    private String username;
    @NotNull(message = "Please enter imageUrl")
    @NotBlank(message = "Please enter imageUrl")
    private String imageUrl;

}
