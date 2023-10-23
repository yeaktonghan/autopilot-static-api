package com.kshrd.autopilot.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoPilotResponse<T> {
    private String message;
    private Boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T payload;
}
