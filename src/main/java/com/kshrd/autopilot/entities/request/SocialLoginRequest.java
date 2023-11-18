package com.kshrd.autopilot.entities.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocialLoginRequest {
    private String email;
    private String sub;
    private String name;
    private String picture;
}
