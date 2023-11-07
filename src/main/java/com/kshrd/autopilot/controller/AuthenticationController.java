package com.kshrd.autopilot.controller;

import com.kshrd.autopilot.entities.RefreshToken;
import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.entities.request.RefreshTokenRequest;

import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.jwt.JwtTokenUtil;
import com.kshrd.autopilot.entities.request.AuthenticationRequest;
import com.kshrd.autopilot.entities.request.ResetPasswordRequest;
import com.kshrd.autopilot.entities.request.UserRequest;
import com.kshrd.autopilot.response.AutoPilotResponse;
import com.kshrd.autopilot.response.OtpResponse;
import com.kshrd.autopilot.service.RefreshTokenService;
import com.kshrd.autopilot.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final UserService service;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    @Value("${error.url}")
    private String errorUrl;

    public AuthenticationController(UserService service, JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.service = service;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }


    @PostMapping("/login")
    public ResponseEntity<AutoPilotResponse<UserDto>> authenticationToken(@Valid @RequestBody UserRequest request) throws Exception {
        authenticate(request.getUsername(), request.getPassword());
        final UserDetails userDetails = service.loadUserByUsername(request.getUsername());

       final   String token = jwtTokenUtil.generateToken(userDetails);


        UserDto userDto = service.getUserByUsername(request.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getUsername());
        userDto.setToken(refreshToken.getToken());
        userDto.setAccessToken(token);

        AutoPilotResponse<UserDto> response = AutoPilotResponse.<UserDto>builder()
                .success(true)
                .message("You are successfully logged in")
                .payload(userDto).build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token){
         UserDto userDto=service.confirmEmail(token);
        AutoPilotResponse<UserDto> response = AutoPilotResponse.<UserDto>builder()
                .success(true)
                .message("Email verified successfully!")
                .payload(userDto).build();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        AtomicReference<UserDto> userDto = new AtomicReference<>(new UserDto());
        refreshTokenService.findToken(request.getToken())
                .map(refreshTokenService::verifyToken)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserDetails userDetails = service.loadUserByUsername(user.getUsername());
                    String accessToken = jwtTokenUtil.generateToken(userDetails);
                    userDto.set(service.getUserByUsername(user.getUsername()));
                    userDto.get().setAccessToken(accessToken);
                    userDto.get().setToken(request.getToken());
                    return userDto;
                }).orElseThrow(() -> new RuntimeException("Refrest token is not in database!"));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<AutoPilotResponse<UserDto>> registration(@Valid @RequestBody AuthenticationRequest request,HttpServletRequest httpServletRequest) throws MessagingException {
        UserDto userDto = service.registration(request,httpServletRequest);
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
        service.sendOTP(username, request);
        OtpResponse response = OtpResponse.builder().status(true).message("OTP has been sent to your email").build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verifyOTP")
    public ResponseEntity<?> verifyOTP(@RequestParam("otp") Integer otp) {
        service.verifyOTP(otp);
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
            UserDetails userDetails=service.loadUserByUsername(username);
            if (!userDetails.isEnabled()){
                throw new AutoPilotException("Email confirm!", HttpStatus.UNAUTHORIZED, errorUrl, "Please confirm your account by email");

            }else
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new AutoPilotException("Incorrect Password!", HttpStatus.UNAUTHORIZED, errorUrl, "Your password is incorrect!");
            } else {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
                authenticationManager.authenticate(authToken);
            }
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}



