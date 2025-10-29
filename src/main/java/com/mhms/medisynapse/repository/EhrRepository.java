package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Ehr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EhrRepository extends JpaRepository<Ehr, Long> {
    @Query("SELECT e FROM Ehr e WHERE e.patient.id = :patientId")
    List<Ehr> findAllByPatientId(@Param("patientId") Long patientId);
}

