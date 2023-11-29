package com.kshrd.autopilot.service;

import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.entities.request.AuthenticationRequest;

import com.kshrd.autopilot.entities.request.ResetPasswordRequest;
import com.kshrd.autopilot.entities.request.SocialLoginRequest;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.net.MalformedURLException;

public interface UserService extends UserDetailsService {
    UserDto getUserByUsername(String username);
    UserDto registration(AuthenticationRequest request,HttpServletRequest requestSer) throws MessagingException, MalformedURLException;
    UserDto confirmEmail(String token);
    void sendOTP(String username, HttpServletRequest request) throws MessagingException;
    UserDto verifyOTP(Integer otp);
    UserDto resetPassword(ResetPasswordRequest request);
    UserDto fromSocial(SocialLoginRequest request);


}
