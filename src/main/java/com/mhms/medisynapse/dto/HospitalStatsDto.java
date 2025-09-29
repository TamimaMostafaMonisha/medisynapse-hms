package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalStatsDto {
    private Long totalHospitals;
    private Long activeHospitals;
    private Long inactiveHospitals;
    private Long totalBeds;
    private Long availableBeds;
    private BigDecimal occupancyRate;
    private Long totalStaff;
    private Integer averageBedsPerHospital;
    private Map<String, Long> departmentDistribution;
    private Map<String, Long> statusDistribution;
}
