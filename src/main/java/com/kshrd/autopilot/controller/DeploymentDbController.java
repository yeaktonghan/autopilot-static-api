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
import java.net.URISyntaxException;

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
    public ResponseEntity<?> getDatabase(@PathVariable("id") Long projectId) {
        AutoPilotResponse<?> response = AutoPilotResponse.builder()
                .success(true)
                .message("Get all Database deployments successfully")
                .payload( service.getDeploymentDatabaseByProjectId(projectId))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/database/{id}")
    public ResponseEntity<?> deleteDatabase(@PathVariable("id") Long databaseId, @RequestParam Long projectId, @RequestParam Boolean deleteBackup) throws JSchException, URISyntaxException, IOException, InterruptedException {
        AutoPilotResponse<?> response = AutoPilotResponse.builder()
                .success(true)
                .message("Database deleted.")
                .payload(service.deleteDatabaseByDatabaseId(databaseId, projectId, deleteBackup))
                .build();
        return ResponseEntity.ok(response);
    }
}
