package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {

    @Query("SELECT i FROM Insurance i WHERE i.policyNumber = :policyNumber AND i.isActive = true")
    Optional<Insurance> findByPolicyNumber(@Param("policyNumber") String policyNumber);

    @Query("SELECT i FROM Insurance i WHERE i.provider = :provider AND i.isActive = true")
    List<Insurance> findByProvider(@Param("provider") String provider);

    @Query("SELECT i FROM Insurance i WHERE i.isActive = true")
    List<Insurance> findAllActive();
}

