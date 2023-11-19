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
    private Long projectId;
    private String domain;
    private String token;
    @NotNull
    private String framework;
    @NotNull
    private String buildTool;
    private String email;
    private String description;
    @NotNull
    private String gitPlatform;
    @NotNull
    private String gitSrcUrl;
    private Integer projectPort;
    private String path;
    private Integer dependsOn;
    @NotNull
    private String branch;

}
