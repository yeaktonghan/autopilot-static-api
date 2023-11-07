package com.kshrd.autopilot.controller;

import com.kshrd.autopilot.entities.dto.ProjectDto;
import com.kshrd.autopilot.entities.request.CreateTeamRequest;
import com.kshrd.autopilot.response.AutoPilotResponse;
import com.kshrd.autopilot.service.ProjectService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@CrossOrigin
@SecurityRequirement(name = "auth")
public class ProjectController {
    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping("/project")
    public ResponseEntity<AutoPilotResponse<ProjectDto>> createProject(@Valid @RequestBody CreateTeamRequest request) {
        ProjectDto project = service.createProject(request);
        AutoPilotResponse<ProjectDto> response = AutoPilotResponse.<ProjectDto>
                        builder()
                .success(true)
                .message("Project has bean created successfully")
                .payload(project).build();
        return ResponseEntity.ok(response);
    }
    @GetMapping ("/project/{id}")
    public ResponseEntity<AutoPilotResponse<ProjectDto>> getProject(@PathVariable("id") Integer id) {
        ProjectDto project = service.getProjectById(id);
        AutoPilotResponse<ProjectDto> response = AutoPilotResponse.<ProjectDto>
                        builder()
                .success(true)
                .message("Get project by project id is successfully")
                .payload(project).build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/project")
    public ResponseEntity<AutoPilotResponse<List<ProjectDto>>> getAllProjectByUser() {
        List<ProjectDto> projects = service.getProjectByUser();
        AutoPilotResponse<List<ProjectDto>> response = AutoPilotResponse.<List<ProjectDto>>
                        builder()
                .success(true)
                .message("Get all your projects")
                .payload(projects).build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/project/{id}")
    public ResponseEntity<AutoPilotResponse<ProjectDto>> editProjectByUser(@Valid @RequestBody CreateTeamRequest request, @PathVariable("id") Integer id) {
        ProjectDto project = service.editProject(request, id);
        AutoPilotResponse<ProjectDto> response = AutoPilotResponse.<ProjectDto>
                        builder()
                .success(true)
                .message("Project has been updated successfully")
                .payload(project).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/project/{code}")
    public ResponseEntity<AutoPilotResponse<ProjectDto>> joinProject(@PathVariable("code") String code) {
        AutoPilotResponse<ProjectDto> response = AutoPilotResponse.<ProjectDto>builder()
                .success(true)
                .message("You have been joined project")
                .payload(service.joinProject(code)).build();
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/project/{id}")
    public ResponseEntity<AutoPilotResponse<ProjectDto>> removeProject(@PathVariable("id") Integer id) {
        service.removeProject(id);
        AutoPilotResponse<ProjectDto> response = AutoPilotResponse.<ProjectDto>builder()
                .success(true)
                .message("Project has been removed successfully")
                .payload(null).build();
        return ResponseEntity.ok(response);
    }
}
