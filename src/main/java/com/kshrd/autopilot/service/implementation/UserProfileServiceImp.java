package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.entities.request.PasswordRequest;
import com.kshrd.autopilot.entities.request.UpdateUserRequest;
import com.kshrd.autopilot.entities.user.User;
import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.repository.UserRepository;
import com.kshrd.autopilot.service.UserProfileService;
import com.kshrd.autopilot.util.CurrentUserUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

    @Override
    public UserDto removeProfileImage(HttpServletRequest request) throws MalformedURLException {
        String email=CurrentUserUtil.getEmail();
        User user=repository.findUsersByEmail(email);
        String imagePf[] = {
                "userPf1.png", "userPf2.png", "userPf3.png", "userPf4.png", "userPf5.png", "userPf6.png",
                "userPf7.png", "userPf8.png", "userPf9.png", "userPf10.png", "userPf11.png", "userPf12.png",
                "userPf13.png", "userPf14.png", "userPf15.png", "userPf16.png"
        };
        Random random=new Random();
        int index=random.nextInt(imagePf.length);
        URL url = new URL(String.valueOf(request.getRequestURL()));
        String baseUrl = url.getProtocol() + "://" + url.getHost() +":8080"+ "/";
        user.setImageUrl(baseUrl + "api/v1/file/profile?filePf=" +imagePf[index]);
        return repository.save(user).toUserDto();
    }

    @Override
    public UserDto changeUserProfile(String imageUrl) {
        String email=CurrentUserUtil.getEmail();
        User user =repository.findUsersByEmail(email);
        user.setImageUrl(imageUrl);
        return repository.save(user).toUserDto();
    }
}
