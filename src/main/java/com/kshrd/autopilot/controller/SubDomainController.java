package com.kshrd.autopilot.controller;

import com.kshrd.autopilot.entities.dto.SubDomainDto;
import com.kshrd.autopilot.response.AutoPilotResponse;
import com.kshrd.autopilot.service.SubDomainService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@SecurityRequirement(name = "auth")
public class SubDomainController {
    private final SubDomainService service;

    public SubDomainController(SubDomainService service) {
        this.service = service;
    }

    @GetMapping("/subdomain/")
    public ResponseEntity<AutoPilotResponse<List<SubDomainDto>>> getAllSubDomain(){
        AutoPilotResponse<List<SubDomainDto>> response = AutoPilotResponse.<List<SubDomainDto>>builder()
                .success(true)
                .message("Get all subdomain.")
                .payload(service.getAllSubDomain())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subdomain/isNotTaken")
    public ResponseEntity<AutoPilotResponse<List<SubDomainDto>>> getAvailableSubDomain(){
        AutoPilotResponse<List<SubDomainDto>> response = AutoPilotResponse.<List<SubDomainDto>>builder()
                .success(true)
                .message("Get all subdomain.")
                .payload(service.getAvailableSubDomain())
                .build();
        return ResponseEntity.ok(response);
    }
}
