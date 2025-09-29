package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.HospitalDto;
import com.mhms.medisynapse.dto.HospitalStatsDto;
import com.mhms.medisynapse.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HospitalService {

    /**
     * Create a new hospital
     */
    HospitalDto createHospital(HospitalDto hospitalDto);

    /**
     * Update an existing hospital
     */
    HospitalDto updateHospital(Long id, HospitalDto hospitalDto);

    /**
     * Soft delete a hospital by setting isActive = false
     */
    void deleteHospital(Long id);

    /**
     * Search hospitals by name (case-insensitive, partial matching, active only)
     */
    List<HospitalDto> searchHospitalsByName(String name);

    /**
     * Get comprehensive hospital statistics for dashboard
     */
    HospitalStatsDto getHospitalStatistics();

    /**
     * Get all active hospitals with pagination
     */
    PagedResponse<HospitalDto> getAllActiveHospitals(Pageable pageable);

    /**
     * Get all hospitals with pagination
     */
    PagedResponse<HospitalDto> getAllHospitals(Pageable pageable);

    /**
     * Get hospitals with filters and pagination
     */
    PagedResponse<HospitalDto> getHospitalsWithFilters(String name, String contact, Pageable pageable);

    /**
     * Get hospital by ID
     */
    HospitalDto getHospitalById(Long id);

    /**
     * Get active hospital by ID
     */
    HospitalDto getActiveHospitalById(Long id);
}
