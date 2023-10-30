package com.kshrd.autopilot.entities.request;

import com.kshrd.autopilot.entities.user.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String fullName;
    private String username;
    private String imageUrl;
    private Gender gender;
}
