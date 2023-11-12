package com.kshrd.autopilot.entities.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentDBRequest {
    private String dbType;
    private Integer project_id;
    private String dbName;
    @Schema(hidden = true)
    private String username;
    private String password;
}
