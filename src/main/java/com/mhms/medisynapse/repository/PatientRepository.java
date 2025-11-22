package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT COUNT(p) FROM Patient p " +
            "JOIN p.patientHospitals ph " +
            "WHERE ph.hospital.id = :hospitalId AND p.isActive = true")
    Long countPatientsByHospitalId(@Param("hospitalId") Long hospitalId);

    @Query("SELECT p FROM Patient p " +
            "JOIN p.patientHospitals ph " +
            "WHERE ph.hospital.id = :hospitalId AND p.createdDt >= :admissionTime " +
            "ORDER BY p.createdDt DESC")
    List<Patient> findRecentAdmissionsByHospitalId(@Param("hospitalId") Long hospitalId,
                                                   @Param("admissionTime") LocalDateTime admissionTime);

    @Query("SELECT p FROM Patient p " +
            "JOIN FETCH p.patientHospitals ph " +
            "LEFT JOIN FETCH p.address " +
            "WHERE (:hospitalId IS NULL OR ph.hospital.id = :hospitalId) " +
            "AND p.isActive = true " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(p.contact) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(p.nationalId) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Patient> findPatientsByHospitalId(@Param("hospitalId") Long hospitalId,
                                           @Param("status") Patient.PatientStatus status,
                                           @Param("search") String search,
                                           Pageable pageable);

    @Query("SELECT p FROM Patient p " +
            "LEFT JOIN FETCH p.address " +
            "WHERE p.id = :patientId AND p.isActive = true")
    Patient findPatientById(@Param("patientId") Long patientId);

    // Doctor-specific patient queries
    @Query("SELECT DISTINCT p FROM Patient p " +
            "LEFT JOIN FETCH p.address " +
            "WHERE p.id IN (" +
            "   SELECT DISTINCT a.patient.id FROM Appointment a " +
            "   WHERE a.doctor.id = :doctorId AND a.isActive = true" +
            ") " +
            "AND p.isActive = true " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:search IS NULL OR :search = '' OR " +
            "     LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(p.contact) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Patient> findPatientsByDoctor(
            @Param("doctorId") Long doctorId,
            @Param("status") Patient.PatientStatus status,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT DISTINCT p FROM Patient p " +
            "LEFT JOIN FETCH p.address " +
            "WHERE p.id IN (" +
            "   SELECT DISTINCT a.patient.id FROM Appointment a " +
            "   WHERE a.doctor.id = :doctorId " +
            "   AND a.isActive = true " +
            "   ORDER BY a.lastUpdatedDt DESC" +
            ") " +
            "AND p.isActive = true " +
            "ORDER BY p.lastUpdatedDt DESC")
    List<Patient> findRecentPatientsByDoctor(@Param("doctorId") Long doctorId, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT p.id) FROM Patient p " +
            "WHERE p.id IN (" +
            "   SELECT DISTINCT a.patient.id FROM Appointment a " +
            "   WHERE a.doctor.id = :doctorId AND a.isActive = true" +
            ") " +
            "AND p.isActive = true")
    Long countPatientsByDoctor(@Param("doctorId") Long doctorId);

    @Query("SELECT p FROM Patient p WHERE (:status IS NULL OR p.status = :status) AND " +
            "(:search IS NULL OR " +
            "     LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(p.contact) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Patient> findAllPatientsWithFilters(
            @Param("status") Patient.PatientStatus status,
            @Param("search") String search,
            Pageable pageable);

    // 1. Patients for a hospital, filtered by PatientHospital.status
    @Query("SELECT p FROM Patient p JOIN p.patientHospitals ph WHERE ph.hospital.id = :hospitalId AND (:status IS NULL OR ph.status = :status) AND p.isActive = true AND ph.isActive = true")
    Page<Patient> findPatientsByHospitalAndStatus(@Param("hospitalId") Long hospitalId, @Param("status") com.mhms.medisynapse.entity.PatientHospital.PatientHospitalStatus status, Pageable pageable);

    // 2. Patients eligible for assignment to a hospital (not active in that hospital)
    @Query("SELECT p FROM Patient p WHERE p.isActive = true AND NOT EXISTS (SELECT 1 FROM PatientHospital ph WHERE ph.patient = p AND ph.hospital.id = :hospitalId AND ph.status = 'ACTIVE' AND ph.isActive = true)")
    Page<Patient> findEligiblePatientsForHospital(@Param("hospitalId") Long hospitalId, Pageable pageable);

    // 3. All patients (global list, optionally filter by isActive)
    Page<Patient> findAllByIsActiveTrue(Pageable pageable);

    Page<Patient> findAll(Pageable pageable);

    java.util.Optional<Patient> findByNationalId(String nationalId);
}
