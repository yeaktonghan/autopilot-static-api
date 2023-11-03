package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.entities.request.PasswordRequest;
import com.kshrd.autopilot.entities.request.UpdateUserRequest;
import com.kshrd.autopilot.entities.user.User;
import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.repository.UserRepository;
import com.kshrd.autopilot.service.UserProfileService;
import com.kshrd.autopilot.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileServiceImp implements UserProfileService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    @Value("${error.url}")
    private String urlError;

    public UserProfileServiceImp(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto changPassword(PasswordRequest request) {
        String email= CurrentUserUtil.getEmail();
        String newPassoword=passwordEncoder.encode(request.getNewPassword());
        User user=repository.findUsersByEmail(email);
        if (passwordEncoder.matches(request.getNewPassword(),user.getPassword())) {
            throw new AutoPilotException("same password", HttpStatus.BAD_REQUEST,urlError,"You new password same as old passowrd");
        }else if (passwordEncoder.matches(request.getOldPassword(),user.getPassword())){
            user.setPassword(newPassoword);
            repository.save(user);
        }else {
            throw new AutoPilotException("Incorrect password", HttpStatus.FORBIDDEN,urlError,"Incorrect old password");
        }

        return user.toUserDto();
    }

    @Override
    public UserDto updateProfile(UpdateUserRequest request) {
        String email=CurrentUserUtil.getEmail();
        List<User> users = repository.findAll();
        for (User user:users){
            if (user.getUsername().equals(request.getUsername()) && !user.getEmail().equals(email)){
                throw new AutoPilotException("Already used",HttpStatus.BAD_REQUEST,urlError,"Username is token choose another");
            }
        }
        User user=repository.findUsersByEmail(email);
        user.setUsername(request.getUsername());
        user.setFull_name(request.getFullName());
        user.setImageUrl(request.getImageUrl());
        repository.save(user);
        return user.toUserDto();
    }

    @Override
    public UserDto getCurrentUser() {
        String email= CurrentUserUtil.getEmail();
        User user=repository.findUsersByEmail(email);
        return user.toUserDto();
    }

    @Override
    public UserDto getUserById(Integer id) {

        return null;
    }
}
