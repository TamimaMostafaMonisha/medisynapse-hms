package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("SELECT d FROM Department d WHERE d.hospital.id = :hospitalId AND d.isActive = true")
    List<Department> findByHospitalId(@Param("hospitalId") Long hospitalId);

    @Query("SELECT d FROM Department d WHERE d.id = :departmentId AND d.hospital.id = :hospitalId AND d.isActive = true")
    Optional<Department> findByIdAndHospitalId(@Param("departmentId") Long departmentId, @Param("hospitalId") Long hospitalId);

    @Query("SELECT d FROM Department d WHERE d.name = :name AND d.hospital.id = :hospitalId AND d.isActive = true")
    Optional<Department> findByNameAndHospitalId(@Param("name") String name, @Param("hospitalId") Long hospitalId);
}
