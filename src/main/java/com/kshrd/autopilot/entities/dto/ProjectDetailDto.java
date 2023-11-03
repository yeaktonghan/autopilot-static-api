package com.kshrd.autopilot.entities.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProjectDetailDto {
    private List<ProjectDto> projects;
    private List<UserDto> members;
    private Boolean isOwner;

    public ProjectDetailDto(List<ProjectDto> projects, List<UserDto> members, Boolean isOwner) {
        this.projects = projects;
        this.members = members;
        this.isOwner = isOwner;
    }
}
