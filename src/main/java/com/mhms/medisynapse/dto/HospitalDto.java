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
public class HospitalDto {
    private Long id;
    private String name;
    private String contact;
    private AddressDto address;
    private LocalDateTime createdDt;
    private LocalDateTime lastUpdatedDt;
}
