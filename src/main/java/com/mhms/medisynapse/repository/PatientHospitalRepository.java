package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.PatientHospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for PatientHospital entity.
 */
@Repository
public interface PatientHospitalRepository extends JpaRepository<PatientHospital, Long> {
}

