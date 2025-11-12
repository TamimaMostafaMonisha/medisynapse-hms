package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceClaimResponseDto {
    private Long id;
    private Long billingId;
    private Long policyId;
    private String policyNumber;
    private String provider;
    private BigDecimal claimAmount;
    private String status;
    private LocalDateTime submittedDt;
    private LocalDateTime settledDt;
}

