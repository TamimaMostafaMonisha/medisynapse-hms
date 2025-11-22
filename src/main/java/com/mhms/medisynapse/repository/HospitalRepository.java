package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("SELECT h FROM Hospital h LEFT JOIN FETCH h.address WHERE " +
            "(:name IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:contact IS NULL OR LOWER(h.contact) LIKE LOWER(CONCAT('%', :contact, '%')))")
    Page<Hospital> findHospitalsWithFilters(@Param("name") String name,
                                            @Param("contact") String contact,
                                            Pageable pageable);

    @Query("SELECT h FROM Hospital h LEFT JOIN FETCH h.address")
    Page<Hospital> findAllWithAddress(Pageable pageable);

    @Query("SELECT h FROM Hospital h LEFT JOIN FETCH h.address WHERE h.isActive = true")
    Page<Hospital> findAllActiveWithAddress(Pageable pageable);

    @Query("SELECT h FROM Hospital h LEFT JOIN FETCH h.address WHERE h.id = :id AND h.isActive = true")
    Optional<Hospital> findActiveHospitalById(@Param("id") Long id);

    @Query("SELECT h FROM Hospital h LEFT JOIN FETCH h.address WHERE h.isActive = true AND LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Hospital> searchActiveHospitalsByName(@Param("name") String name);

    // Statistics queries
    @Query("SELECT COUNT(h) FROM Hospital h")
    Long countTotalHospitals();

    @Query("SELECT COUNT(h) FROM Hospital h WHERE h.isActive = true")
    Long countActiveHospitals();

    @Query("SELECT COUNT(h) FROM Hospital h WHERE h.isActive = false")
    Long countInactiveHospitals();

    @Query("SELECT COALESCE(SUM(h.totalBeds), 0) FROM Hospital h WHERE h.isActive = true")
    Long sumTotalBeds();

    @Query("SELECT COALESCE(SUM(h.availableBeds), 0) FROM Hospital h WHERE h.isActive = true")
    Long sumAvailableBeds();

    @Query("SELECT COALESCE(SUM(h.totalStaff), 0) FROM Hospital h WHERE h.isActive = true")
    Long sumTotalStaff();

    @Query("SELECT COALESCE(AVG(h.totalBeds), 0) FROM Hospital h WHERE h.isActive = true")
    Integer averageBedsPerHospital();

    @Query("SELECT h.status, COUNT(h) FROM Hospital h GROUP BY h.status")
    List<Object[]> getStatusDistribution();

    @Query("SELECT d.name, COUNT(d) FROM Department d JOIN d.hospital h WHERE h.isActive = true GROUP BY d.name ORDER BY COUNT(d) DESC")
    List<Object[]> getDepartmentDistribution();

    @Query("SELECT h FROM Hospital h LEFT JOIN FETCH h.address WHERE h.id IN :hospitalIds ORDER BY h.name")
    List<Hospital> findAvailableHospitalsByIds(@Param("hospitalIds") List<Long> hospitalIds);

    @Query("SELECT h FROM Hospital h LEFT JOIN FETCH h.address WHERE h.isActive = true ORDER BY h.name")
    List<Hospital> findAllActiveHospitalsWithAddress();
}
