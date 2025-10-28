package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.LabTestOrder;
import com.mhms.medisynapse.entity.LabTestOrder.LabTestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabTestOrderRepository extends JpaRepository<LabTestOrder, Long> {

    /**
     * Find lab orders by appointment
     */
    @Query("SELECT l FROM LabTestOrder l WHERE l.appointment.id = :appointmentId " +
            "AND l.isActive = true ORDER BY l.orderedAt DESC")
    List<LabTestOrder> findByAppointmentIdOrderByOrderedAtDesc(@Param("appointmentId") Long appointmentId);

    /**
     * Find lab orders by patient
     */
    @Query("SELECT l FROM LabTestOrder l WHERE l.patient.id = :patientId " +
            "AND l.isActive = true ORDER BY l.orderedAt DESC")
    List<LabTestOrder> findByPatientIdOrderByOrderedAtDesc(@Param("patientId") Long patientId);

    /**
     * Find lab orders by doctor
     */
    @Query("SELECT l FROM LabTestOrder l WHERE l.doctor.id = :doctorId " +
            "AND l.isActive = true ORDER BY l.orderedAt DESC")
    List<LabTestOrder> findByDoctorIdOrderByOrderedAtDesc(@Param("doctorId") Long doctorId);

    /**
     * Find pending lab results for doctor (for review)
     */
    @Query("SELECT l FROM LabTestOrder l WHERE l.doctor.id = :doctorId " +
            "AND l.status IN ('COMPLETED', 'IN_PROGRESS') " +
            "AND (l.reviewedAt IS NULL OR l.reviewedBy IS NULL) " +
            "AND l.isActive = true " +
            "ORDER BY l.orderedAt DESC")
    List<LabTestOrder> findPendingReviewByDoctor(@Param("doctorId") Long doctorId);

    /**
     * Find lab orders by status
     */
    @Query("SELECT l FROM LabTestOrder l WHERE l.status = :status " +
            "AND l.isActive = true ORDER BY l.orderedAt DESC")
    List<LabTestOrder> findByStatusOrderByOrderedAtDesc(@Param("status") LabTestStatus status);

    /**
     * Find lab orders by prescription
     */
    @Query("SELECT l FROM LabTestOrder l WHERE l.prescription.id = :prescriptionId " +
            "AND l.isActive = true ORDER BY l.orderedAt DESC")
    List<LabTestOrder> findByPrescriptionId(@Param("prescriptionId") Long prescriptionId);

    /**
     * Find lab orders by status for hospital admin
     */
    @Query("SELECT l FROM LabTestOrder l " +
            "WHERE l.hospital.id = :hospitalId " +
            "AND l.status IN :statuses " +
            "AND l.isActive = true " +
            "ORDER BY l.orderedAt DESC")
    List<LabTestOrder> findByHospitalAndStatuses(
            @Param("hospitalId") Long hospitalId,
            @Param("statuses") List<LabTestStatus> statuses
    );

    /**
     * Count pending lab orders for appointment
     */
    @Query("SELECT COUNT(l) FROM LabTestOrder l WHERE l.appointment.id = :appointmentId " +
            "AND l.status IN ('ORDERED', 'SAMPLE_COLLECTED', 'IN_PROGRESS') " +
            "AND l.isActive = true")
    Long countPendingByAppointment(@Param("appointmentId") Long appointmentId);

    /**
     * Count completed but not reviewed lab orders for doctor
     */
    @Query("SELECT COUNT(l) FROM LabTestOrder l WHERE l.doctor.id = :doctorId " +
            "AND l.status = 'COMPLETED' " +
            "AND (l.reviewedAt IS NULL OR l.reviewedBy IS NULL) " +
            "AND l.isActive = true")
    Long countPendingReviewByDoctor(@Param("doctorId") Long doctorId);
}

