package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.DepartmentTypeDto;

import java.util.List;

public interface DepartmentTypeService {

    /**
     * Get all available department types for selection
     */
    List<DepartmentTypeDto> getAllAvailableDepartmentTypes();
}
