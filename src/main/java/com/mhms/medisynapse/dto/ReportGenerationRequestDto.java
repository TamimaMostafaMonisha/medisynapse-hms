package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportGenerationRequestDto {
    private Long billingId;
    private Long claimId;
    private String reportType; // INVOICE, CLAIM_REPORT, SETTLEMENT_REPORT, etc.
}

