package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.PatientHospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for PatientHospital entity.
 */
@Repository
public interface PatientHospitalRepository extends JpaRepository<PatientHospital, Long> {
    Optional<PatientHospital> findByPatientIdAndHospitalId(Long patientId, Long hospitalId);
}
