package com.kshrd.autopilot.repository;

import com.kshrd.autopilot.entities.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileStorage,Integer> {
}
