package com.kshrd.autopilot.controller;

import com.kshrd.autopilot.entities.request.ImageRequest;
import com.kshrd.autopilot.entities.request.PasswordRequest;
import com.kshrd.autopilot.entities.request.UpdateUserRequest;
import com.kshrd.autopilot.response.AutoPilotResponse;
import com.kshrd.autopilot.service.UserProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;

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
    public ResponseEntity<?>changeProfile(@RequestBody @Valid UpdateUserRequest request){
        AutoPilotResponse<?> response=AutoPilotResponse.
                builder()
                .message("Your profile has been changed")
                .success(true)
                .payload(service.updateProfile(request)).build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/changeProfile/image")
    public ResponseEntity<?>changeProfileImage(@RequestBody ImageRequest imageUrl){
        AutoPilotResponse<?> response=AutoPilotResponse.
                builder()
                .message("Your profile image has been changed")
                .success(true)
                .payload(service.changeUserProfile(imageUrl.getImageUrl())).build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/changeProfile/remove")
    public ResponseEntity<?>removeProfileImage(HttpServletRequest request) throws MalformedURLException {
        AutoPilotResponse<?> response=AutoPilotResponse.
                builder()
                .message("Your profile image has been removed")
                .success(true)
                .payload(service.removeProfileImage(request)).build();
        return ResponseEntity.ok(response);
    }
}
