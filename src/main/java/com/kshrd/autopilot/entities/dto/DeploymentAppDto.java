package com.kshrd.autopilot.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentAppDto {
    private String appName;
    private String domain;
    private String ipAddress;
    private String port;
    private Integer projectPort;
    private String path;
    private String framework;
    private String buildTool;
    private String email;
    private String description;
    private String gitPlatform;
    private String gitSrcUrl;
    private Integer dependsOn;
    private  String project;
    private String branch;
    private LocalDateTime createAt;
}
