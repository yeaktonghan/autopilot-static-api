package com.kshrd.autopilot.controller;

import com.jcraft.jsch.JSchException;
import com.kshrd.autopilot.entities.dto.DeploymentAppDto;
import com.kshrd.autopilot.entities.request.DeploymentAppRequest;
import com.kshrd.autopilot.response.AutoPilotResponse;
import com.kshrd.autopilot.service.DeploymentAppService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/deploymentApp")
@SecurityRequirement(name = "auth")
public class DeploymentAppController {
    private final DeploymentAppService service;

    public DeploymentAppController(DeploymentAppService service) {
        this.service = service;
    }

    @PostMapping("/deployment")
    public ResponseEntity<AutoPilotResponse<DeploymentAppDto>> createDeploymentApp(@RequestBody @Valid DeploymentAppRequest request) throws IOException, InterruptedException {
        //System.out.println("what wrong");
        AutoPilotResponse<DeploymentAppDto> response = AutoPilotResponse.<DeploymentAppDto>builder()
                .success(true)
                .message("DeploymentApp has created successfully")
                .payload(service.createDeploymentApp(request))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/deployment/{project_id}")
    public ResponseEntity<AutoPilotResponse<List<DeploymentAppDto>>> getAllDeployment(@PathVariable("project_id") Long project_id) {
        List<DeploymentAppDto> deploymentAppDtos = service.getAllDeploymentApps(project_id);
        AutoPilotResponse<List<DeploymentAppDto>> response = AutoPilotResponse.<List<DeploymentAppDto>>
                        builder()
                .success(true)
                .message("Get all deployments by project")
                .payload(deploymentAppDtos).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deployment/{id}")
    public ResponseEntity<?> getDeploymentById(@PathVariable("id") Integer id) {
        AutoPilotResponse<DeploymentAppDto> response = AutoPilotResponse.<DeploymentAppDto>
                        builder()
                .success(true)
                .message("Get  deployments by ID successfully")
                .payload(service.getDeploymentAppById(id)).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deployment/console/{id}")
    public ResponseEntity<?> getConsoleDeploymentById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.getConsoleBuildByDeploymentId(id));
    }

    @DeleteMapping(path = "/deployment/{id}/delete")
    public ResponseEntity<?> deleteDeploymentById(@PathVariable("id") Long id) throws JSchException, IOException {
        AutoPilotResponse<String> response = AutoPilotResponse.<String>builder()
                .success(true)
                .message("Successfully deleted App Deployment")
                .payload(service.deleteAppDeploymentById(id))
                .build();
        return ResponseEntity.ok(response);
    }
}
