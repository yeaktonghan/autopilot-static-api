package com.kshrd.autopilot.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class ProjectBranch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String branch;
    @ManyToOne
    @JoinColumn(name = "deployment_app_id",referencedColumnName = "id")
    private DeploymentApp deploymentApp;
}
