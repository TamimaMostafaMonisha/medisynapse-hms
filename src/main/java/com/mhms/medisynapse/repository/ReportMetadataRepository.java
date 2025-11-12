package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.ReportMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportMetadataRepository extends JpaRepository<ReportMetadata, Long> {

    @Query("SELECT rm FROM ReportMetadata rm WHERE rm.billing.id = :billingId AND rm.isActive = true")
    List<ReportMetadata> findByBillingId(@Param("billingId") Long billingId);

    @Query("SELECT rm FROM ReportMetadata rm WHERE rm.claim.id = :claimId AND rm.isActive = true")
    List<ReportMetadata> findByClaimId(@Param("claimId") Long claimId);

    @Query("SELECT rm FROM ReportMetadata rm WHERE rm.reportType = :reportType AND rm.isActive = true ORDER BY rm.generatedAt DESC")
    List<ReportMetadata> findByReportType(@Param("reportType") String reportType);
}

