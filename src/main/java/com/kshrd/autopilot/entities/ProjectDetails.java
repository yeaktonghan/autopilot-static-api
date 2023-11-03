package com.kshrd.autopilot.entities;

import com.kshrd.autopilot.entities.dto.ProjectDetailDto;
import com.kshrd.autopilot.entities.dto.ProjectDto;
import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "project_detail")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne()
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;
    @ManyToOne()
    @JoinColumn(name = "project_id",referencedColumnName = "id")
    private Project project;
    private Boolean is_owner=false;

    public ProjectDetailDto projectDetailDto(List<ProjectDto> projectDtos, List<UserDto> userDtos){
        return new ProjectDetailDto(projectDtos, userDtos ,is_owner);
    }
}
