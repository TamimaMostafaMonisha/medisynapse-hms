package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Long> {

    @Query("SELECT a FROM Admission a " +
            "JOIN FETCH a.patient p " +
            "JOIN FETCH a.department d " +
            "WHERE a.hospital.id = :hospitalId " +
            "AND a.createdDt >= :fromDateTime " +
            "AND a.isActive = true " +
            "ORDER BY a.createdDt DESC")
    List<Admission> findRecentAdmissionsByHospitalId(@Param("hospitalId") Long hospitalId,
                                                     @Param("fromDateTime") LocalDateTime fromDateTime);

    @Query("SELECT COUNT(a) FROM Admission a " +
            "WHERE a.hospital.id = :hospitalId " +
            "AND a.status = 'ADMITTED' " +
            "AND a.dischargeDt IS NULL " +
            "AND a.isActive = true")
    Long countCurrentlyAdmittedPatientsByHospitalId(@Param("hospitalId") Long hospitalId);

    @Query("SELECT COUNT(a) FROM Admission a " +
            "WHERE a.department.id = :departmentId " +
            "AND DATE(a.createdDt) = CURRENT_DATE " +
            "AND a.isActive = true")
    Long countTodayAdmissionsByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT a FROM Admission a " +
            "JOIN FETCH a.patient p " +
            "JOIN FETCH a.department d " +
            "WHERE a.hospital.id = :hospitalId " +
            "AND a.status = 'ADMITTED' " +
            "AND a.dischargeDt IS NULL " +
            "AND a.bedNo IS NOT NULL " +
            "AND a.isActive = true " +
            "ORDER BY d.name, a.bedNo")
    List<Admission> findCurrentAdmissionsByHospitalIdWithBeds(@Param("hospitalId") Long hospitalId);

    @Query("SELECT DISTINCT a.bedNo FROM Admission a " +
            "WHERE a.hospital.id = :hospitalId " +
            "AND a.status = 'ADMITTED' " +
            "AND a.dischargeDt IS NULL " +
            "AND a.bedNo IS NOT NULL " +
            "AND a.isActive = true")
    List<String> findOccupiedBedNumbersByHospitalId(@Param("hospitalId") Long hospitalId);

    @Query("SELECT a FROM Admission a " +
            "JOIN FETCH a.patient p " +
            "WHERE a.hospital.id = :hospitalId " +
            "AND a.department.id = :departmentId " +
            "AND a.status = 'ADMITTED' " +
            "AND a.dischargeDt IS NULL " +
            "AND a.bedNo IS NOT NULL " +
            "AND a.isActive = true " +
            "ORDER BY a.bedNo")
    List<Admission> findCurrentAdmissionsByHospitalAndDepartmentId(
            @Param("hospitalId") Long hospitalId,
            @Param("departmentId") Long departmentId);

    @Query("SELECT a FROM Admission a " +
            "WHERE a.patient.id = :patientId " +
            "AND a.status = 'ADMITTED' " +
            "AND a.dischargeDt IS NULL " +
            "AND a.isActive = true " +
            "ORDER BY a.createdDt DESC")
    List<Admission> findCurrentAdmissionByPatientId(@Param("patientId") Long patientId);
}
