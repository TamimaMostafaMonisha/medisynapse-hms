package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.BedManagementResponseDto;

public interface BedService {
    BedManagementResponseDto getBedOccupancyStatus(Long hospitalId);
}
