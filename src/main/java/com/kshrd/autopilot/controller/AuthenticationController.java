package com.kshrd.autopilot.controller;

import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.jwt.JwtTokenUtil;
import com.kshrd.autopilot.entities.request.AuthenticationRequest;
import com.kshrd.autopilot.entities.request.ResetPasswordRequest;
import com.kshrd.autopilot.entities.request.UserRequest;
import com.kshrd.autopilot.response.AutoPilotResponse;
import com.kshrd.autopilot.response.OtpResponse;
import com.kshrd.autopilot.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final UserService service;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(UserService service, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager1, JwtTokenUtil jwtTokenUtil1, AuthenticationManager authenticationManager2) {
        this.service = service;
        this.jwtTokenUtil = jwtTokenUtil1;
        this.authenticationManager = authenticationManager2;
    }

    @PostMapping("/login")
    public ResponseEntity<AutoPilotResponse<UserDto>> authenticationToken(@Valid @RequestBody UserRequest request) throws Exception {
        authenticate(request.getUsername(), request.getPassword());
        final UserDetails userDetails = service.loadUserByUsername(request.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        UserDto userDto = service.getUserByUsername(request.getUsername());
        userDto.setToken(token);
        AutoPilotResponse<UserDto> response = AutoPilotResponse.<UserDto>builder()
                .success(true)
                .message("You are successfully logged in")
                .payload(userDto).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AutoPilotResponse<UserDto>> registration(@Valid @RequestBody AuthenticationRequest request) {
        UserDto userDto = service.registration(request);
        AutoPilotResponse<UserDto> response = AutoPilotResponse.<UserDto>builder()
                .message("Your registration was successfully")
                .success(true)
                .payload(userDto).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sendOTP")
    public ResponseEntity<?> sendOTP(@RequestParam String username, HttpServletRequest request) throws MessagingException {
        //ystem.out.println(String.valueOf(request.getRequestURL()).substring(0, 22));
        //URL url=new URL(request.getRequestURL());
       service.sendOTP(username,request);
        OtpResponse response = OtpResponse.builder().status(true).message("OTP has been sent to your email").build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verifyOTP")
    public ResponseEntity<?> verifyOTP(@RequestParam("otp")Integer otp) {
        System.out.println("This otp="+otp);
       // service.verifyOTP(otp);
        OtpResponse response = OtpResponse.builder().status(true).message("OTP has been verified").build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<AutoPilotResponse<UserDto>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        UserDto userDto = service.resetPassword(request);
        AutoPilotResponse<UserDto> response = AutoPilotResponse.<UserDto>builder()
                .success(true)
                .message("Your password has been updated")
                .payload(userDto)
                .build();
        return ResponseEntity.ok(response);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}



