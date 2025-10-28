package com.mhms.medisynapse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabTestOrderRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotBlank(message = "Test name is required")
    private String testName;

    @NotBlank(message = "Test type is required")
    private String testType;  // Blood, Urine, Imaging, Biopsy, Other

    @NotBlank(message = "Urgency is required")
    private String urgency;  // ROUTINE, URGENT, STAT

    private String clinicalNotes;

    private String suspectedDiagnosis;
}

