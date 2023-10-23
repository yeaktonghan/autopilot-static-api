package com.kshrd.autopilot.controller;

import com.kshrd.autopilot.entities.request.PasswordRequest;
import com.kshrd.autopilot.entities.request.UpdateUserRequest;
import com.kshrd.autopilot.response.AutoPilotResponse;
import com.kshrd.autopilot.service.SetUpProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/setup")
@SecurityRequirement(name = "auth")
public class SetUpProfileController {
    private final SetUpProfileService service;


    public SetUpProfileController(SetUpProfileService service) {
        this.service = service;
    }
    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordRequest request){
        AutoPilotResponse<?> response=AutoPilotResponse.
                builder()
                .message("Your password has been changed")
                .success(true)
                .payload(service.changPassword(request)).build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/changeProfile")
    public ResponseEntity<?>changeProfile(@RequestBody UpdateUserRequest request){
        AutoPilotResponse<?> response=AutoPilotResponse.
                builder()
                .message("Your password has been changed")
                .success(true)
                .payload(service.updateProfile(request)).build();
        return ResponseEntity.ok(response);
    }
}
