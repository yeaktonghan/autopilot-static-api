package com.kshrd.autopilot.entities;

import com.kshrd.autopilot.entities.dto.DeploymentAppDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeploymentApp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    @Column(unique = true)
    private String gitSrcUrl;
    private String token;
    private Boolean status;
    private Integer dependsOn;
    private String branch;
    private LocalDateTime createAt;
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    public DeploymentAppDto toDeploymentAppDto() {
        return new DeploymentAppDto(this.appName, this.domain, this.ipAddress, this.port,this.projectPort,this.path, this.framework, this.buildTool, this.email, this.description, this.gitPlatform, this.gitSrcUrl, this.dependsOn, this.project.getName(), this.branch, this.createAt);
    }
}
