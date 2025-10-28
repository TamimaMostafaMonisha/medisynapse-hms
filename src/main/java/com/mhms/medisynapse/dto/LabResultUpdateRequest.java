package com.mhms.medisynapse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabResultUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;  // SAMPLE_COLLECTED, IN_PROGRESS, COMPLETED

    private String reportFileUrl;
}

