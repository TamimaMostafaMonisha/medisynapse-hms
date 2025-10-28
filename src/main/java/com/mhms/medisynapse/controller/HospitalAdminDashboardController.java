package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.constants.SuccessMessages;
import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.BedManagementResponseDto;
import com.mhms.medisynapse.dto.DashboardStatisticsDto;
import com.mhms.medisynapse.dto.DashboardStatisticsRequestDto;
import com.mhms.medisynapse.dto.DepartmentPerformanceResponseDto;
import com.mhms.medisynapse.dto.HospitalAdminResponseDto;
import com.mhms.medisynapse.security.CustomUserDetails;
import com.mhms.medisynapse.service.BedService;
import com.mhms.medisynapse.service.DashboardService;
import com.mhms.medisynapse.service.DepartmentPerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hospital-admin")
@RequiredArgsConstructor
@Slf4j
public class HospitalAdminDashboardController {

    private final DashboardService dashboardService;
    private final BedService bedService;
    private final DepartmentPerformanceService departmentPerformanceService;

    @GetMapping(value = "/dashboard/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DashboardStatisticsDto>> getDashboardStatistics(
            @RequestParam Long hospitalId) {

        log.info("Fetching dashboard statistics for hospital ID: {}",
                hospitalId);

        DashboardStatisticsRequestDto request = new DashboardStatisticsRequestDto();
        request.setHospitalId(hospitalId);

        DashboardStatisticsDto statistics = dashboardService.getDashboardStatistics(request);

        log.info("Dashboard statistics retrieved successfully for hospital ID: {}", hospitalId);

        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.DASHBOARD_STATISTICS_RETRIEVED, statistics)
        );
    }

    @GetMapping(value = "/beds", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BedManagementResponseDto>> getBedOccupancyStatus(
            @RequestParam Long hospitalId) {

        log.info("Fetching bed occupancy status for hospital ID: {}", hospitalId);

        BedManagementResponseDto bedStatus = bedService.getBedOccupancyStatus(hospitalId);

        log.info("Bed occupancy status retrieved successfully for hospital ID: {}", hospitalId);

        return ResponseEntity.ok(
                ApiResponse.success("Bed occupancy status retrieved successfully", bedStatus)
        );
    }

    @GetMapping(value = "/departments/performance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DepartmentPerformanceResponseDto>> getDepartmentPerformance(
            @RequestParam Long hospitalId) {

        log.info("Fetching department performance for hospital ID: {}", hospitalId);

        DepartmentPerformanceResponseDto performanceData = departmentPerformanceService.getDepartmentPerformance(hospitalId);

        log.info("Department performance data retrieved successfully for hospital ID: {}", hospitalId);

        return ResponseEntity.ok(
                ApiResponse.success("Department performance metrics retrieved successfully", performanceData)
        );
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<HospitalAdminResponseDto>> getAuthenticatedAdminProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized"));
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (!"HOSPITAL_ADMIN".equals(userDetails.getRole().name())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Forbidden: Not a hospital admin"));
        }
        HospitalAdminResponseDto profile = dashboardService.getHospitalAdminProfile(userDetails.getId());
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Profile not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", profile));
    }
}
