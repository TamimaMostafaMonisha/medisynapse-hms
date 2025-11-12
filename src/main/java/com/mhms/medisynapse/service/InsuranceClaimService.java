package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.InsuranceClaimRequestDto;
import com.mhms.medisynapse.dto.InsuranceClaimResponseDto;
import com.mhms.medisynapse.dto.InsuranceSettlementRequestDto;
import com.mhms.medisynapse.entity.InsuranceClaim;
import com.mhms.medisynapse.entity.InsuranceSettlement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InsuranceClaimService {

    InsuranceClaim createClaim(InsuranceClaimRequestDto request, Long createdBy);

    InsuranceClaim getClaimById(Long claimId);

    InsuranceClaimResponseDto getClaimResponseById(Long claimId);

    InsuranceClaim updateClaimStatus(Long claimId, InsuranceClaim.ClaimStatus status, Long updatedBy);

    InsuranceSettlement createSettlement(InsuranceSettlementRequestDto request, Long createdBy);

    List<InsuranceSettlement> getSettlementsByClaimId(Long claimId);

    Page<InsuranceClaim> getClaimsByStatus(InsuranceClaim.ClaimStatus status, Pageable pageable);

    List<InsuranceClaim> getClaimsByPatientId(Long patientId);

    List<InsuranceClaim> getClaimsByPolicyId(Long policyId);
}

