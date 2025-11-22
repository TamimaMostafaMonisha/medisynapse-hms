package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Billing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    @Query("SELECT b FROM Billing b WHERE b.patient.id = :patientId AND b.isActive = true")
    Page<Billing> findByPatientIdAndIsActiveTrue(@Param("patientId") Long patientId, Pageable pageable);

    @Query("SELECT b FROM Billing b WHERE b.patient.id = :patientId AND b.status = :status AND b.isActive = true")
    Page<Billing> findByPatientIdAndStatusAndIsActiveTrue(@Param("patientId") Long patientId, @Param("status") Billing.BillingStatus status, Pageable pageable);

    @Query("SELECT b FROM Billing b WHERE b.hospital.id = :hospitalId AND b.isActive = true")
    Page<Billing> findByHospitalIdAndIsActiveTrue(@Param("hospitalId") Long hospitalId, Pageable pageable);

    @Query("SELECT b FROM Billing b WHERE b.hospital.id = :hospitalId AND b.status = :status AND b.isActive = true")
    Page<Billing> findByHospitalIdAndStatusAndIsActiveTrue(@Param("hospitalId") Long hospitalId, @Param("status") Billing.BillingStatus status, Pageable pageable);

    @Query("SELECT b FROM Billing b WHERE b.status = :status AND b.isActive = true")
    Page<Billing> findByStatusAndIsActiveTrue(@Param("status") Billing.BillingStatus status, Pageable pageable);

    @Query("SELECT b FROM Billing b WHERE b.isActive = true")
    Page<Billing> findAllActiveBillings(Pageable pageable);
}
