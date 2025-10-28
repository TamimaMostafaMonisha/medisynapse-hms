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
public class LabTestOrderResponse {

    private Long id;
    private Long patientId;
    private Long appointmentId;
    private Long doctorId;
    private Long prescriptionId;

    private String testName;
    private String testType;
    private String urgency;
    private String status;

    private String clinicalNotes;
    private String suspectedDiagnosis;

    private LocalDateTime orderedAt;
    private LocalDateTime sampleCollectedAt;
    private LocalDateTime completedAt;
    private LocalDateTime reviewedAt;
    private Long reviewedBy;

    private String reportFileUrl;
    private Long uploadedBy;
    private LocalDateTime uploadedAt;
}

