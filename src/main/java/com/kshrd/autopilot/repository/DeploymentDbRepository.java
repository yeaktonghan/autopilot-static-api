package com.kshrd.autopilot.repository;

import com.kshrd.autopilot.entities.DeploymentDb;
import com.kshrd.autopilot.entities.Project;
import com.kshrd.autopilot.entities.dto.DeploymentDBDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeploymentDbRepository extends JpaRepository<DeploymentDb, Integer> {
    @Query(value = "SELECT port FROM deployment_db WHERE project_id = :projectId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Integer findLastPort(Long projectId);
//    DeploymentDb findTopDeploymentDbByProjectOrderByCreated_atDesc(Project project);

    DeploymentDb findDeploymentDbByPort(String port);

    DeploymentDb findDeploymentDbByDbNameAndProject(String dbName, Project project);
    List<DeploymentDb> findAllByProject(Project project);

    @Query(value = "select exists (select * from deployment_db dd where id = :id and project_id = :projectId)", nativeQuery = true)
    Boolean existsByIdAndProjectId(Long id, Long projectId);

    @Modifying
    @Query(value = "update deployment_db set is_deleted = false where id = :id", nativeQuery = true)
    void disableDatabase(Long id);
}
