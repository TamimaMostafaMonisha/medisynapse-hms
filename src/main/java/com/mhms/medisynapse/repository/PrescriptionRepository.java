package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @Query("SELECT p FROM Prescription p " +
            "WHERE p.patient.id = :patientId " +
            "AND p.doctor.id = :doctorId " +
            "AND p.isActive = true " +
            "ORDER BY p.prescriptionDate DESC")
    List<Prescription> findByPatientAndDoctor(@Param("patientId") Long patientId,
                                              @Param("doctorId") Long doctorId);

    @Query("SELECT p FROM Prescription p " +
            "WHERE p.patient.id = :patientId " +
            "AND p.doctor.id = :doctorId " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND p.isActive = true " +
            "ORDER BY p.prescriptionDate DESC")
    Page<Prescription> findByPatientAndDoctorWithFilter(
            @Param("patientId") Long patientId,
            @Param("doctorId") Long doctorId,
            @Param("status") Prescription.PrescriptionStatus status,
            Pageable pageable);

    @Query("SELECT p FROM Prescription p " +
            "WHERE p.appointment.id = :appointmentId " +
            "AND p.doctor.id = :doctorId " +
            "AND p.isActive = true " +
            "ORDER BY p.createdDt DESC")
    List<Prescription> findByAppointmentAndDoctor(
            @Param("appointmentId") Long appointmentId,
            @Param("doctorId") Long doctorId);

    @Query("SELECT p FROM Prescription p " +
            "WHERE p.id = :prescriptionId " +
            "AND p.doctor.id = :doctorId " +
            "AND p.isActive = true")
    Optional<Prescription> findByIdAndDoctor(
            @Param("prescriptionId") Long prescriptionId,
            @Param("doctorId") Long doctorId);

    @Query("SELECT p FROM Prescription p " +
            "WHERE p.patient.id = :patientId " +
            "AND p.hospital.id = :hospitalId " +
            "AND p.isActive = true " +
            "ORDER BY p.prescriptionDate DESC")
    List<Prescription> findByPatientAndHospital(
            @Param("patientId") Long patientId,
            @Param("hospitalId") Long hospitalId);

    /**
     * Find prescriptions by appointment and prescription type
     */
    @Query("SELECT p FROM Prescription p WHERE p.appointment.id = :appointmentId " +
            "AND p.prescriptionType = :type " +
            "AND p.isActive = true " +
            "ORDER BY p.createdDt DESC")
    List<Prescription> findByAppointmentAndType(
            @Param("appointmentId") Long appointmentId,
            @Param("type") Prescription.PrescriptionType type
    );

    /**
     * Find active prescriptions not superseded
     */
    @Query("SELECT p FROM Prescription p WHERE p.appointment.id = :appointmentId " +
            "AND p.status = 'ACTIVE' AND p.supersededBy IS NULL " +
            "AND p.isActive = true " +
            "ORDER BY p.createdDt DESC")
    List<Prescription> findActiveByAppointment(@Param("appointmentId") Long appointmentId);

    /**
     * Find all prescriptions by appointment (for history)
     */
    @Query("SELECT p FROM Prescription p WHERE p.appointment.id = :appointmentId " +
            "AND p.isActive = true ORDER BY p.createdDt DESC")
    List<Prescription> findByAppointmentIdOrderByCreatedDtDesc(@Param("appointmentId") Long appointmentId);

    /**
     * Count prescriptions by appointment
     */
    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.appointment.id = :appointmentId " +
            "AND p.isActive = true")
    Long countByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Query("SELECT p FROM Prescription p WHERE p.doctor.id = :doctorId AND p.hospital.id = :hospitalId AND p.isActive = true ORDER BY p.prescriptionDate DESC")
    Page<Prescription> findByDoctorAndHospital(@Param("doctorId") Long doctorId,
                                               @Param("hospitalId") Long hospitalId,
                                               Pageable pageable);
}
