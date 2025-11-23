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
public class PrescriptionResponseDto {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long appointmentId;
    private Long doctorId;
    private String doctorName;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
    private String notes;
    private LocalDateTime prescribedDate;
    private String status;
    private Integer refillsRemaining;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long hospitalId;
    private String hospitalName;
}
