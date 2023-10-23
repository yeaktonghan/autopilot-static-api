package com.kshrd.autopilot.entities;

import com.kshrd.autopilot.entities.dto.ProjectDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.List;
import java.util.Set;
@Entity
@Table(name = "project")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Column(unique = true)
    private String project_code;
    private LocalDateTime created_at=LocalDateTime.now();
    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL)
    private Set<ProjectDetails> projectDetails =new HashSet<>();
    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL)
    private List<DeploymentApp> deploymentApps=new ArrayList<>();
    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL)
    private List<DeploymentDb> deploymentDbs=new ArrayList<>();
    public ProjectDto toProjectDto(){
        return new ProjectDto(this.id,this.name,this.project_code,this.created_at);
    }
}
