package com.kshrd.autopilot.repository;

import com.kshrd.autopilot.entities.DeploymentDb;
import com.kshrd.autopilot.entities.Project;
import com.kshrd.autopilot.entities.dto.DeploymentDBDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentDbRepository extends JpaRepository<DeploymentDb, Integer> {
    @Query(value = "SELECT port FROM deployment_db WHERE project_id = :projectId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Integer findLastPort(Integer projectId);
//    DeploymentDb findTopDeploymentDbByProjectOrderByCreated_atDesc(Project project);

    DeploymentDb findDeploymentDbByPort(String port);

    DeploymentDb findDeploymentDbByDbNameAndProject(String dbName, Project project);
}
