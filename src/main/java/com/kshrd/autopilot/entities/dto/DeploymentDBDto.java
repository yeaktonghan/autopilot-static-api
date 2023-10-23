package com.kshrd.autopilot.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentDBDto {
    private String database_name;
    private String connection_host;
    private String port;
    private String type;
    private String username;
    private String password;
    private String project_name;
    private LocalDateTime create_at;
}
