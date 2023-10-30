package com.kshrd.autopilot.service;

import com.kshrd.autopilot.entities.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String username);
    Optional<RefreshToken> findToken(String token);
    RefreshToken verifyToken(RefreshToken token);
}
