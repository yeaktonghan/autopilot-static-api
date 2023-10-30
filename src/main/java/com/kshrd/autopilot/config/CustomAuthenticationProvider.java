package com.kshrd.autopilot.config;

import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;



public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserService service;
    @Value("${error.url}")
    private String errorUrl;
    public CustomAuthenticationProvider(UserService service) {
        this.service = service;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String password=authentication.getCredentials().toString();
        String username=authentication.getName();
        UserDetails userDetail= service.loadUserByUsername(username);
        if (!passwordMatches(password,userDetail.getPassword())){
            throw new AutoPilotException("Incorrect Password!", HttpStatus.BAD_REQUEST,errorUrl,"Your password is incorrect!");
        }
        return new UsernamePasswordAuthenticationToken(username,password,userDetail.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return  UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
    private boolean passwordMatches(String rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword);
    }
}
