package com.kshrd.autopilot.service;

import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.entities.request.PasswordRequest;
import com.kshrd.autopilot.entities.request.UpdateUserRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface UserProfileService {
    UserDto changPassword(PasswordRequest request);
    UserDto updateProfile(UpdateUserRequest request);
    UserDto getCurrentUser();
    UserDto getUserById(Integer id);
    UserDto removeProfileImage(HttpServletRequest request);
    UserDto changeUserProfile(String imageUrl);

}
