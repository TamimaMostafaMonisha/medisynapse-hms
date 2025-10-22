package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    @Query("SELECT COUNT(DISTINCT s.user.id) FROM Shift s " +
            "WHERE s.hospital.id = :hospitalId " +
            "AND s.status = 'ON_DUTY' " +
            "AND :currentTime BETWEEN s.startTime AND s.endTime " +
            "AND s.isActive = true")
    Long countStaffOnDutyByHospitalId(@Param("hospitalId") Long hospitalId,
                                      @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT COUNT(DISTINCT u.id) FROM User u " +
            "WHERE u.hospital.id = :hospitalId " +
            "AND u.role IN ('DOCTOR', 'NURSE') " +
            "AND u.isActive = true")
    Long countTotalStaffByHospitalId(@Param("hospitalId") Long hospitalId);

    @Query("SELECT COUNT(DISTINCT u.id) FROM User u " +
            "WHERE u.hospital.id = :hospitalId " +
            "AND u.role IN ('DOCTOR', 'NURSE') " +
            "AND u.isActive = true " +
            "AND NOT EXISTS (SELECT s FROM Shift s " +
            "                WHERE s.user.id = u.id " +
            "                AND s.status = 'ON_DUTY' " +
            "                AND :currentTime BETWEEN s.startTime AND s.endTime " +
            "                AND s.isActive = true)")
    Long countStaffOffDutyByHospitalId(@Param("hospitalId") Long hospitalId,
                                       @Param("currentTime") LocalDateTime currentTime);
}
