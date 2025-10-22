package com.mhms.medisynapse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsRequestDto {
    @NotNull(message = "Hospital ID is required")
    private Long hospitalId;
}
