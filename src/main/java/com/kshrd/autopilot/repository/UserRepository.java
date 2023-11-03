package com.kshrd.autopilot.repository;

import com.kshrd.autopilot.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
   User findByUsername(String username);
   User findUsersByEmail(String email);
}
