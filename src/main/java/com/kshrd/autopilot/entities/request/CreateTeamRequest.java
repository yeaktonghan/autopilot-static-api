package com.kshrd.autopilot.entities.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTeamRequest {
    @NotNull(message = "Project name can not null")
    @NotBlank(message = "Project name can not blank")
    private String name;
}
