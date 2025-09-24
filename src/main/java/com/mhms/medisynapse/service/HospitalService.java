package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.HospitalDto;
import com.mhms.medisynapse.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface HospitalService {

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
}
