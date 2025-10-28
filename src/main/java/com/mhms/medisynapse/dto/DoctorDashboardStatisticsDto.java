package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDashboardStatisticsDto {
    private Long totalPatients;
    private Long todayAppointments;
    private Long upcomingAppointments;
    private Long completedAppointments;
    private Long pendingAppointments;
}