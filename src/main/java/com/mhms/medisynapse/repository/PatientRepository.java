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
            "WHERE ph.hospital.id = :hospitalId " +
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
            "JOIN FETCH p.patientHospitals ph " +
            "LEFT JOIN FETCH p.address " +
            "WHERE p.id = :patientId AND ph.hospital.id = :hospitalId AND p.isActive = true")
    Patient findPatientByIdAndHospitalId(@Param("patientId") Long patientId,
                                         @Param("hospitalId") Long hospitalId);
}
