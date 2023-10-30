package com.kshrd.autopilot.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProjectDto {
    private Integer project_id;
    private String name;
    private String project_code;
    private Integer member;
    private LocalDateTime created_at;

    public ProjectDto(Integer project_id, String name, String project_code, LocalDateTime created_at) {
        this.project_id = project_id;
        this.name = name;
        this.project_code = project_code;
        this.created_at = created_at;
    }
}
