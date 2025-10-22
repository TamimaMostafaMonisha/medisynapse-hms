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
public class RecentAdmissionDto {
    private Long patientId;
    private String patientName;
    private String roomNumber;
    private String department;
    private LocalDateTime admissionTime;
}