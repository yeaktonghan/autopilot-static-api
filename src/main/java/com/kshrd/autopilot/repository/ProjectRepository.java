package com.kshrd.autopilot.repository;

import com.kshrd.autopilot.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Project findByProjectCode(String code);
    void deleteById(Long id);
    Project findByName(String name);
    }
