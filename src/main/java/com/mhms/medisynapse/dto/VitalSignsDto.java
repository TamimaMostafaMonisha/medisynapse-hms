package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VitalSignsDto {
    private String bloodPressure;
    private String heartRate;
    private String temperature;
    private String weight;
    private String height;
    private LocalDateTime recordedAt;
}

