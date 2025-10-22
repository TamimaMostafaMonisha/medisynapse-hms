package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsDto {
    private Long totalPatients;
    private Long totalDoctors;
    private Long activeAppointments;
    private BedOccupancyDto bedOccupancy;
    private StaffOnDutyDto staffOnDuty;
    private List<DepartmentStatsDto> departmentStats;
    private List<RecentAdmissionDto> recentAdmissions;
}
