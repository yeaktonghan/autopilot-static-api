package com.kshrd.autopilot.service;

import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.entities.request.AuthenticationRequest;

import com.kshrd.autopilot.entities.request.ResetPasswordRequest;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto getUserByUsername(String username);
    UserDto registration(AuthenticationRequest request,HttpServletRequest requestSer) throws MessagingException;
    UserDto confirmEmail(String token);
    void sendOTP(String username, HttpServletRequest request) throws MessagingException;
    void verifyOTP(Integer otp);
    UserDto resetPassword(ResetPasswordRequest request);


}
