package com.kshrd.autopilot.service;

import com.kshrd.autopilot.entities.dto.ProjectDto;
import com.kshrd.autopilot.entities.request.CreateTeamRequest;

import java.util.List;

public interface ProjectService {
    ProjectDto createProject(CreateTeamRequest request);

    List<ProjectDto> getProjectByUser();
    ProjectDto getProjectById(Long id);

    ProjectDto editProject(CreateTeamRequest request, Long id);

    ProjectDto joinProject(String project_code);
    void removeProject(Long id);
}
