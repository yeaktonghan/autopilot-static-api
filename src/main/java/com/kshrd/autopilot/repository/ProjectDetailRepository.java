package com.kshrd.autopilot.repository;

import com.kshrd.autopilot.entities.Project;
import com.kshrd.autopilot.entities.ProjectDetails;
import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectDetailRepository extends JpaRepository<ProjectDetails,Integer> {
    List<ProjectDetails> findAllByUser(User user);
    ProjectDetails findByProject(Project project);
    ProjectDetails findByUserAndProject(User user,Project project);
    List<ProjectDetails> findAllByProject(Project project);

    @Query(value = "select exists (select * from project_detail pd where user_id = :userId and project_id = :projectId)", nativeQuery = true)
    Boolean existsByUserIdAndProjectId(Long userId, Long projectId);
}
