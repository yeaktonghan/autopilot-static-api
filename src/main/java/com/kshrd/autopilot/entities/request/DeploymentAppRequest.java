package com.kshrd.autopilot.entities.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentAppRequest {
    private String appName;
    private Integer project_id;
    private String domain;
    private String token;
    private String framework;
    private String build_tool;
    private String email;
    private String description;
    private String git_platform;
    private String git_src_url;
    private Integer depends_on;
    private String branch;

}
