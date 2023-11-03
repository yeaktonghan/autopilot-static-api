package com.kshrd.autopilot.repository;
import com.kshrd.autopilot.entities.DeploymentApp;
import com.kshrd.autopilot.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeploymentAppRepository extends JpaRepository<DeploymentApp,Integer> {
    List<DeploymentApp> findAllByProject(Project project);
   // Optional<DeploymentApp> findTopByOrderByCreate_atDesc();

    DeploymentApp findByGitSrcUrl(String gitSrcUrl);
}
