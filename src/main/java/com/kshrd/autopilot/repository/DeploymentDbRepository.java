package com.kshrd.autopilot.repository;

import com.kshrd.autopilot.entities.DeploymentDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentDbRepository extends JpaRepository<DeploymentDb,Integer> {
}
