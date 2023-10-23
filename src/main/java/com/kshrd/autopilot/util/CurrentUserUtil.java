package com.kshrd.autopilot.util;


import com.kshrd.autopilot.entities.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserUtil {
    public static String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof User) {
                return ((User) authentication.getPrincipal()).getEmail();
            }
        }
        return null; // No authenticated user or email not available
    }
}
