package com.mhms.medisynapse.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionWithTestsRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Prescription type is required")
    private String prescriptionType;  // PRELIMINARY, FINAL

    @Valid
    private List<MedicationDto> medications;

    @Valid
    private List<LabTestOrderRequest> labTestOrders;

    private String instructions;
    private String notes;
    private String clinicalDiagnosis;
    private Boolean followUpRequired;
    private LocalDate followUpDate;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicationDto {
        @NotNull(message = "Medication name is required")
        private String medicationName;

        @NotNull(message = "Dosage is required")
        private String dosage;

        @NotNull(message = "Frequency is required")
        private String frequency;

        @NotNull(message = "Duration is required")
        private String duration;
    }
}

