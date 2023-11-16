package com.kshrd.autopilot.controller;

import com.jcraft.jsch.JSchException;
import com.kshrd.autopilot.entities.dto.DeploymentAppDto;
import com.kshrd.autopilot.entities.request.DeploymentDBRequest;
import com.kshrd.autopilot.response.AutoPilotResponse;
import com.kshrd.autopilot.service.DeploymentDBService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/v1")
@SecurityRequirement(name = "auth")
public class DeploymentDbController {
    private final DeploymentDBService service;

    public DeploymentDbController(DeploymentDBService service) {
        this.service = service;
    }

    @PostMapping("/database")
    public ResponseEntity<?> createDatabase(@RequestBody DeploymentDBRequest request) throws JSchException, IOException, InterruptedException {

        AutoPilotResponse<?> response = AutoPilotResponse.builder()
                .success(true)
                .message("Deployment database has created successfully")
                .payload( service.deployDatabase(request))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/database/project/{id}")
    public ResponseEntity<?> createDatabase(@PathVariable("id") Long project_id) {

        AutoPilotResponse<?> response = AutoPilotResponse.builder()
                .success(true)
                .message("Get all Database deployments successfully")
                .payload( service.getDeploymentDatabaseByProjectId(project_id))
                .build();
        return ResponseEntity.ok(response);
    }
}
