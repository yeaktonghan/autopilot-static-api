package com.kshrd.autopilot.entities.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ProjectDto {
    private Integer project_id;
    private String name;
    private String project_code;
    private List<UserDto> members;
    private LocalDateTime created_at;
    private String project_pf;
    private String color;
    private Boolean isOwner;

    public ProjectDto(Integer project_id, String name, String project_code, LocalDateTime created_at,String color,String project_pf) {
        this.project_id = project_id;
        this.name = name;
        this.project_code = project_code;
        this.created_at = created_at;
        this.color=color;
        this.project_pf=project_pf;
    }

    public ProjectDto(Integer project_id, String name, String project_code, List<UserDto> members, LocalDateTime created_at, Boolean isOwner, String color, String project_pf) {
        this.project_id = project_id;
        this.name = name;
        this.project_code = project_code;
        this.members = members;
        this.created_at = created_at;
        this.isOwner = isOwner;
        this.project_pf=project_pf;
        this.color=color;
    }
}
