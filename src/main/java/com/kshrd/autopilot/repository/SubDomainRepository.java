package com.kshrd.autopilot.repository;


import com.kshrd.autopilot.entities.SubDomain;
import org.aspectj.weaver.ast.Var;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubDomainRepository extends JpaRepository<SubDomain,Long> {
    @Query(value = "SELECT * FROM sub_domain WHERE is_taken = false", nativeQuery = true)
    List<SubDomain> findAllByIsTakenFalse();

    @Query(value = "SELECT exists(SELECT * FROM sub_domain WHERE subdomain= :subdomain AND is_validated = true)", nativeQuery = true)
    boolean checkIfSubDomainIsValidated(@Param("subdomain") String subdomain);

    @Query(value = "SELECT exists(SELECT * FROM sub_domain WHERE subdomain= :domain)", nativeQuery = true)
    boolean checkIfExist(@Param("domain") String domain);

    @Modifying
    @Query(value = "UPDATE sub_domain SET is_taken = true WHERE subdomain = :subdomain", nativeQuery = true)
    void setIsTakenToTrue(@Param("subdomain") String subdomain);

    @Modifying
    @Query(value = "UPDATE sub_domain SET is_taken = FALSE WHERE subdomain = :subdomain", nativeQuery = true)
    void setIsTakenToFalse(@Param("subdomain") String subdomain);

    @Modifying
    @Query(value = "UPDATE sub_domain SET is_validated = TRUE WHERE subdomain = :subdomain", nativeQuery = true)
    void setIsValidatedToTrue(@Param("subdomain") String subdomain);

    SubDomain findBySubdomain(String subdomain);
}
