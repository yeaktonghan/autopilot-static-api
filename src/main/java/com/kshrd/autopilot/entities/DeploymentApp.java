package com.kshrd.autopilot.entities;

import com.kshrd.autopilot.entities.dto.DeploymentAppDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeploymentApp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
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
    @OneToOne
    @JoinColumn(name = "token_id",referencedColumnName = "id")
    private Token token_id;
    private Boolean status;
    private Integer depends_on;
    private String branch;
    private LocalDateTime create_at;
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    public DeploymentAppDto toDeploymentAppDto() {
        return new DeploymentAppDto(this.appName,this.domain,this.ipAddress,this.port,this.framework,this.build_tool,this.email,this.description,this.git_platform,this.git_src_url,this.token_id.getUsername(),this.depends_on,this.project.getName(),this.branch,this.create_at);
    }
}
