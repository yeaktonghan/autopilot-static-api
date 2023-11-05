package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.entities.RefreshToken;
import com.kshrd.autopilot.entities.user.User;
import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.repository.RefreshTokenRepository;
import com.kshrd.autopilot.repository.UserRepository;
import com.kshrd.autopilot.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImp implements RefreshTokenService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository repository;

    public RefreshTokenServiceImp(UserRepository userRepository, RefreshTokenRepository repository) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    @Override
    public RefreshToken createRefreshToken(String username) {
        User user=userRepository.findByUsername(username);
        Optional<RefreshToken> optionalRefreshToken=repository.findByUser(user);
        if (optionalRefreshToken.isPresent()){
           optionalRefreshToken.get().setToken(UUID.randomUUID().toString());
           optionalRefreshToken.get().setExpireDate(Instant.now().plusMillis(60000));
           return repository.save(optionalRefreshToken.get());
        }
            RefreshToken refreshToken=RefreshToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expireDate(Instant.now().plusMillis(60000))
                    .build();

        return repository.save(refreshToken);
        }



    @Override
    public Optional<RefreshToken> findToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    public RefreshToken verifyToken(RefreshToken token) {
      // Optional<RefreshToken> refreshToken=repository.findByToken(token.getToken());
        if(token.getExpireDate().compareTo(Instant.now())<0){
           // repository.delete(token);
            throw new AutoPilotException("Expire", HttpStatus.BAD_REQUEST, "http://localhost:8080/errors/", "Token expired");
        }
        return token;
    }
}
