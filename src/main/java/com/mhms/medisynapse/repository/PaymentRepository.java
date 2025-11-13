package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByBillingIdAndIsActiveTrue(Long billingId);

    Page<Payment> findByBillingIdAndIsActiveTrue(Long billingId, Pageable pageable);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.billing.id = :billingId AND p.isActive = true")
    BigDecimal sumPaymentsByBillingId(@Param("billingId") Long billingId);

    @Query("SELECT p FROM Payment p WHERE p.billing.patient.id = :patientId AND p.isActive = true ORDER BY p.paymentDate DESC")
    Page<Payment> findByPatientId(@Param("patientId") Long patientId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.billing.hospital.id = :hospitalId AND p.isActive = true ORDER BY p.paymentDate DESC")
    Page<Payment> findByHospitalId(@Param("hospitalId") Long hospitalId, Pageable pageable);
}

