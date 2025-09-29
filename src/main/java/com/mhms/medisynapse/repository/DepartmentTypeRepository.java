package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.DepartmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentTypeRepository extends JpaRepository<DepartmentType, Long> {

    @Query("SELECT dt FROM DepartmentType dt WHERE dt.isActive = true ORDER BY dt.name ASC")
    List<DepartmentType> findAllActiveOrderByName();
}
