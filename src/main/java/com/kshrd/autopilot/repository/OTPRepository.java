package com.kshrd.autopilot.repository;

import com.kshrd.autopilot.entities.OTPstore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTPstore,Integer> {
  Optional<OTPstore> findByUserId(Long userId);
}
