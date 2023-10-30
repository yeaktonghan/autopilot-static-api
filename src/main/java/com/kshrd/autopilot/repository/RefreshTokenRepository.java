package com.kshrd.autopilot.repository;

import com.kshrd.autopilot.entities.RefreshToken;
import com.kshrd.autopilot.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Integer> {

 Optional<RefreshToken> findByToken(String token);
 Optional<RefreshToken> findByUser(User user);

}
