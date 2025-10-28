package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprehensivePrescriptionResponse {

    private Long prescriptionId;
    private List<Long> prescriptionIds;
    private List<Long> labOrderIds;
    private String prescriptionType;
    private String message;
    private Integer medicationCount;
    private Integer labTestCount;
}

