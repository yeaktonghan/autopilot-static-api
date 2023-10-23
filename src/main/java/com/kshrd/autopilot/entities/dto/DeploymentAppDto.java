package com.kshrd.autopilot.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentAppDto {
    private Integer id;
    private String appName;
    private String domain;
    private String ipAddress;
    private String language;
    private String project_name;
    private LocalDateTime create_at;
}
