package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentPerformanceDto {
    private Long departmentId;
    private String name;
    private Long todayPatients;
    private Long monthlyPatients;
    private Long activeDoctors;
    private Double avgWaitTime;
    private Double patientSatisfaction;
}
