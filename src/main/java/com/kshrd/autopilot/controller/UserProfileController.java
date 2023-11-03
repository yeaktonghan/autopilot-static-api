package com.kshrd.autopilot.controller;

import com.kshrd.autopilot.entities.request.PasswordRequest;
import com.kshrd.autopilot.entities.request.UpdateUserRequest;
import com.kshrd.autopilot.response.AutoPilotResponse;
import com.kshrd.autopilot.service.UserProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@SecurityRequirement(name = "auth")
public class UserProfileController {
    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
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
    @GetMapping("/current-user")
    public ResponseEntity<?>getCurrentUser(){
        AutoPilotResponse<?> response=AutoPilotResponse.
                builder()
                .message("Get current user successfully")
                .success(true)
                .payload(service.getCurrentUser()).build();
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
