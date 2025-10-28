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
public class PrescriptionHistoryItem {

    private Long id;
    private Long appointmentId;
    private String prescriptionType;
    private LocalDateTime createdAt;
    private String doctorName;
    private Integer medicationCount;
    private Integer labTestOrderCount;
    private String status;
}

