package com.kshrd.autopilot.repository;

import com.kshrd.autopilot.entities.ConfirmationEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationEmailRepository extends JpaRepository<ConfirmationEmail,Integer> {
    ConfirmationEmail findByConfirmationToken(String token);
}
