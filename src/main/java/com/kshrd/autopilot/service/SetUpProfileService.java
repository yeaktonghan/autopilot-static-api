package com.kshrd.autopilot.service;

import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.entities.request.PasswordRequest;
import com.kshrd.autopilot.entities.request.UpdateUserRequest;

public interface SetUpProfileService {
    UserDto changPassword(PasswordRequest request);
    UserDto updateProfile(UpdateUserRequest request);
}
