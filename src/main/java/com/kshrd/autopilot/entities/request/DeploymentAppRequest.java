package com.kshrd.autopilot.entities.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentAppRequest {
    private String appName;
    @NotNull
    private Integer projectId;
    private String domain;
    private String token;
    private String framework;
    private String buildTool;
    private String email;
    private String description;
    private String gitPlatform;
    private String gitSrcUrl;
    private Integer projectPort;
    private String path;
    private Integer dependsOn;
    private String branch;

}
