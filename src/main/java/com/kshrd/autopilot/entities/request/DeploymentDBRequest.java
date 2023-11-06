package com.kshrd.autopilot.entities.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentDBRequest {
    private String dbType;
    private Integer project_id;
    private String name;
    private String username;
    private String password;
}
