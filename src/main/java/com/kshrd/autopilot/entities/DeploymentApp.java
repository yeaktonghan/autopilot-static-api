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
    private Integer id;
    private String appName;
    private String domain;
    private String ipAddress;
    private String port;
    private String language;
    private LocalDateTime create_at;
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    public DeploymentAppDto toDeploymentAppDto() {
        return new DeploymentAppDto(this.id, this.appName, this.domain, this.ipAddress, this.language, this.project.getName(), this.create_at);
    }
}
