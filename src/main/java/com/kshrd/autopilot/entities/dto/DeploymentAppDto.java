package com.kshrd.autopilot.entities.dto;

import com.kshrd.autopilot.entities.ProjectBranch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentAppDto {
    private String appName;
    private String domain;
    private String ipAddress;
    private String port;
    private String framework;
    private String build_tool;
    private String email;
    private String description;
    private String git_platform;
    private String git_src_url;
    private String token;
    private Integer depends_on;
    private  String project;
    private String branch;
    private LocalDateTime create_at;
}
