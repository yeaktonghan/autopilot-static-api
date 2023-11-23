package com.kshrd.autopilot.repository;

import com.kshrd.autopilot.entities.NotificationCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationCredentialRepository extends JpaRepository<NotificationCredential,Long> {
}
