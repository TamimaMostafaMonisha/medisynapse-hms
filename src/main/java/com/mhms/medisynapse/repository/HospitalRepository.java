package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    @Query("SELECT h FROM Hospital h LEFT JOIN FETCH h.address WHERE " +
            "(:name IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:contact IS NULL OR LOWER(h.contact) LIKE LOWER(CONCAT('%', :contact, '%')))")
    Page<Hospital> findHospitalsWithFilters(@Param("name") String name,
                                            @Param("contact") String contact,
                                            Pageable pageable);

    @Query("SELECT h FROM Hospital h LEFT JOIN FETCH h.address")
    Page<Hospital> findAllWithAddress(Pageable pageable);
}
