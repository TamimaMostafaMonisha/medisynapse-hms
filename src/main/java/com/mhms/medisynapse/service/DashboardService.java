package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.DashboardStatisticsDto;
import com.mhms.medisynapse.dto.DashboardStatisticsRequestDto;
import com.mhms.medisynapse.dto.HospitalAdminResponseDto;
import com.mhms.medisynapse.dto.ReceptionistResponseDto;

public interface DashboardService {
    DashboardStatisticsDto getDashboardStatistics(DashboardStatisticsRequestDto request);

    HospitalAdminResponseDto getHospitalAdminProfile(Long id);

    ReceptionistResponseDto getReceptionistProfile(Long id);
}
