package com.kshrd.autopilot.entities;

import com.kshrd.autopilot.entities.dto.ProjectDto;
import com.kshrd.autopilot.entities.dto.UserDto;

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
    private Long id;
    private String name;
    @Column(unique = true)
    private String projectCode;
    private String projectPf;
    private String color;
    private LocalDateTime created_at=LocalDateTime.now();
    @OneToMany(mappedBy = "project")
    private final Set<ProjectDetails> projectDetails =new HashSet<>();
    @OneToMany(mappedBy = "project")
    private Set<DeploymentApp> deploymentApps=new HashSet<>();
    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL)
    private Set<DeploymentDb> deploymentDbs=new HashSet<>();
    @OneToOne(mappedBy = "project",cascade = CascadeType.ALL)
    private NotificationCredential notificationCredential;
    public ProjectDto toProjectDto(List<UserDto> userDtos, Boolean isOwner){
        return new ProjectDto(this.id,this.name,this.projectCode, userDtos,created_at,isOwner,color, projectPf);
    }
    public ProjectDto toProjectDto(){
        return new ProjectDto(this.id,this.name,this.projectCode,this.created_at,color, projectPf);
    }
}
