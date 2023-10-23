package com.kshrd.autopilot.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OtpResponse {
    private String message;
    private Boolean status;
}
