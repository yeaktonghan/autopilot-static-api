package com.kshrd.autopilot.repository;


import com.kshrd.autopilot.entities.SubDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubDomainRepository extends JpaRepository<SubDomain,Long> {
    SubDomain findTopById(Long id);
}
