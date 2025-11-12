package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {

    @Query("SELECT r FROM Refund r WHERE r.billing.id = :billingId AND r.isActive = true")
    List<Refund> findByBillingId(@Param("billingId") Long billingId);

    @Query("SELECT r FROM Refund r WHERE r.billing.patient.id = :patientId AND r.isActive = true")
    List<Refund> findByPatientId(@Param("patientId") Long patientId);
}

