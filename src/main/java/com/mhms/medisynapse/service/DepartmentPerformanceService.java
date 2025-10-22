package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.DepartmentPerformanceResponseDto;

public interface DepartmentPerformanceService {
    DepartmentPerformanceResponseDto getDepartmentPerformance(Long hospitalId);
}
