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
public class HospitalAdminResponseDto {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String phone;
    private String nationalId;
    private Long hospitalId;
    private String hospitalName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
}
