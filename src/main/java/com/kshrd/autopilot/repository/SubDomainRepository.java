package com.kshrd.autopilot.repository;


import com.kshrd.autopilot.entities.SubDomain;
import org.aspectj.weaver.ast.Var;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubDomainRepository extends JpaRepository<SubDomain,Long> {
    @Query(value = "SELECT * FROM sub_domain WHERE is_taken = false", nativeQuery = true)
    List<SubDomain> findAllByIsTakenFalse();

    @Query(value = "SELECT exists(SELECT * FROM sub_domain WHERE subdomain= :subdomain AND is_validated = true)", nativeQuery = true)
    boolean checkIfSubDomainIsValidated(String subdomain);

    @Query(value = "SELECT exists(SELECT * FROM sub_domain WHERE subdomain= :subdomain)", nativeQuery = true)
    boolean checkIfExist(String domain);

    @Query(value = "UPDATE sub_domain SET is_taken = TRUE WHERE subdomain = :subdomain", nativeQuery = true)
    void setIsTakenToTrue(String subdomain);

    @Query(value = "UPDATE sub_domain SET is_taken = FALSE WHERE subdomain = :subdomain", nativeQuery = true)
    void setIsTakenToFalse(String subdomain);

    @Query(value = "UPDATE sub_domain SET is_validated = TRUE WHERE subdomain = :subdomain", nativeQuery = true)
    void setIsValidatedToTrue(String subdomain);
}
