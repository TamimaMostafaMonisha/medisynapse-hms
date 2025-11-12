package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.InsuranceClaim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, Long> {

    @Query("SELECT ic FROM InsuranceClaim ic WHERE ic.billing.id = :billingId AND ic.isActive = true")
    Optional<InsuranceClaim> findByBillingId(@Param("billingId") Long billingId);

    @Query("SELECT ic FROM InsuranceClaim ic WHERE ic.policy.id = :policyId AND ic.isActive = true")
    List<InsuranceClaim> findByPolicyId(@Param("policyId") Long policyId);

    @Query("SELECT ic FROM InsuranceClaim ic WHERE ic.status = :status AND ic.isActive = true")
    Page<InsuranceClaim> findByStatus(@Param("status") InsuranceClaim.ClaimStatus status, Pageable pageable);

    @Query("SELECT ic FROM InsuranceClaim ic WHERE ic.billing.patient.id = :patientId AND ic.isActive = true")
    List<InsuranceClaim> findByPatientId(@Param("patientId") Long patientId);
}

