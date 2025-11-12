package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.InsuranceSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsuranceSettlementRepository extends JpaRepository<InsuranceSettlement, Long> {

    @Query("SELECT is FROM InsuranceSettlement is WHERE is.claim.id = :claimId AND is.isActive = true")
    List<InsuranceSettlement> findByClaimId(@Param("claimId") Long claimId);

    @Query("SELECT is FROM InsuranceSettlement is WHERE is.claim.billing.id = :billingId AND is.isActive = true")
    List<InsuranceSettlement> findByBillingId(@Param("billingId") Long billingId);
}

