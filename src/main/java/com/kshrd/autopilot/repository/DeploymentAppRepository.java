package com.kshrd.autopilot.repository;
import com.kshrd.autopilot.entities.DeploymentApp;
import com.kshrd.autopilot.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeploymentAppRepository extends JpaRepository<DeploymentApp,Integer> {
    List<DeploymentApp> findAllByProject(Project project);
   // Optional<DeploymentApp> findTopByOrderByCreate_atDesc();

    DeploymentApp findByGitSrcUrl(String gitSrcUrl);

    @Query(value = "select exists (select * from project_detail pd where project_id = (select project_id from deployment_app da where id = :id) and pd.user_id = :userId)", nativeQuery = false)
    boolean checkIfProjectExistForUser(@Param("id") Long id, @Param("userId") Long currentUserId);
}
