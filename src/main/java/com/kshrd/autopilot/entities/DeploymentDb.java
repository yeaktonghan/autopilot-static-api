package com.kshrd.autopilot.entities;

import com.kshrd.autopilot.entities.dto.DeploymentDBDto;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class DeploymentDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String dbName;
    private String ipAddress;
    private String port;
    private String dbType;
    private String dbUsername;
    private String dbPassword;
    private Boolean isDeleted;
    private LocalDateTime created_at;
    @ManyToOne
    @JoinColumn(name = "project_id",referencedColumnName = "id")
    private Project project;

    public DeploymentDBDto toDeploymentDBDto(){
        return new DeploymentDBDto(id, dbName,ipAddress,port,dbType,dbUsername,dbPassword,project.getName(),created_at);
    }

}
