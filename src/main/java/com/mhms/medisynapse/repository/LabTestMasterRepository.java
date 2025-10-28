package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.LabTestMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabTestMasterRepository extends JpaRepository<LabTestMaster, Long> {

    /**
     * Find by test name
     */
    Optional<LabTestMaster> findByTestName(String testName);

    /**
     * Find by test code
     */
    Optional<LabTestMaster> findByTestCode(String testCode);

    /**
     * Find by test type
     */
    @Query("SELECT l FROM LabTestMaster l WHERE l.testType = :testType " +
            "AND l.isActive = true ORDER BY l.testName ASC")
    List<LabTestMaster> findByTestType(@Param("testType") String testType);

    /**
     * Find by category
     */
    @Query("SELECT l FROM LabTestMaster l WHERE l.category = :category " +
            "AND l.isActive = true ORDER BY l.testName ASC")
    List<LabTestMaster> findByCategory(@Param("category") String category);

    /**
     * Find all active tests
     */
    @Query("SELECT l FROM LabTestMaster l WHERE l.isActive = true " +
            "ORDER BY l.testName ASC")
    List<LabTestMaster> findByIsActiveTrueOrderByTestNameAsc();

    /**
     * Search by test name (case-insensitive)
     */
    @Query("SELECT l FROM LabTestMaster l WHERE " +
            "LOWER(l.testName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "AND l.isActive = true ORDER BY l.testName ASC")
    List<LabTestMaster> findByTestNameContainingIgnoreCaseAndIsActiveTrue(@Param("searchTerm") String searchTerm);
}

