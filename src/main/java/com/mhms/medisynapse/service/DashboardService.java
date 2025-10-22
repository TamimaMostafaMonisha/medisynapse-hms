package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.DashboardStatisticsDto;
import com.mhms.medisynapse.dto.DashboardStatisticsRequestDto;

public interface DashboardService {
    DashboardStatisticsDto getDashboardStatistics(DashboardStatisticsRequestDto request);
}
