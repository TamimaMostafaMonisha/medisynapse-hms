package com.mhms.medisynapse.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePrescriptionRequest {

    @Size(min = 2, max = 200, message = "Medication name must be between 2 and 200 characters")
    private String medicationName;

    @Size(max = 100, message = "Dosage must not exceed 100 characters")
    private String dosage;

    @Size(max = 100, message = "Frequency must not exceed 100 characters")
    private String frequency;

    @Size(max = 100, message = "Duration must not exceed 100 characters")
    private String duration;

    @Size(max = 1000, message = "Instructions must not exceed 1000 characters")
    private String instructions;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    private String status;
}

