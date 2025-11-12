package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.InsuranceClaimRequestDto;
import com.mhms.medisynapse.dto.InsuranceClaimResponseDto;
import com.mhms.medisynapse.dto.InsuranceSettlementRequestDto;
import com.mhms.medisynapse.entity.Billing;
import com.mhms.medisynapse.entity.Insurance;
import com.mhms.medisynapse.entity.InsuranceClaim;
import com.mhms.medisynapse.entity.InsuranceSettlement;
import com.mhms.medisynapse.repository.BillingRepository;
import com.mhms.medisynapse.repository.InsuranceClaimRepository;
import com.mhms.medisynapse.repository.InsuranceRepository;
import com.mhms.medisynapse.repository.InsuranceSettlementRepository;
import com.mhms.medisynapse.service.InsuranceClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InsuranceClaimServiceImpl implements InsuranceClaimService {

    private final InsuranceClaimRepository insuranceClaimRepository;
    private final InsuranceSettlementRepository insuranceSettlementRepository;
    private final BillingRepository billingRepository;
    private final InsuranceRepository insuranceRepository;

    @Override
    @Transactional
    public InsuranceClaim createClaim(InsuranceClaimRequestDto request, Long createdBy) {
        log.info("Creating insurance claim for billing ID: {}", request.getBillingId());

        // Validate billing exists
        Billing billing = billingRepository.findById(request.getBillingId())
                .orElseThrow(() -> new RuntimeException("Billing not found with ID: " + request.getBillingId()));

        // Validate insurance policy exists
        Insurance policy = insuranceRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new RuntimeException("Insurance policy not found with ID: " + request.getPolicyId()));

        // Validate policy is active and valid
        LocalDate today = LocalDate.now();
        if (policy.getValidFrom() != null && today.isBefore(policy.getValidFrom())) {
            throw new RuntimeException("Insurance policy is not yet valid");
        }
        if (policy.getValidTo() != null && today.isAfter(policy.getValidTo())) {
            throw new RuntimeException("Insurance policy has expired");
        }

        // Check if claim already exists for this billing
        insuranceClaimRepository.findByBillingId(request.getBillingId())
                .ifPresent(claim -> {
                    throw new RuntimeException("Claim already exists for this billing");
                });

        // Validate claim amount
        if (request.getClaimAmount().compareTo(billing.getNetAmount()) > 0) {
            throw new RuntimeException("Claim amount cannot exceed billing net amount");
        }

        InsuranceClaim claim = new InsuranceClaim();
        claim.setBilling(billing);
        claim.setPolicy(policy);
        claim.setClaimAmount(request.getClaimAmount());
        claim.setStatus(InsuranceClaim.ClaimStatus.SUBMITTED);
        claim.setCreatedBy(createdBy);
        claim.setUpdatedBy(createdBy);

        claim = insuranceClaimRepository.save(claim);
        log.info("Insurance claim created with ID: {}", claim.getId());

        return claim;
    }

    @Override
    public InsuranceClaim getClaimById(Long claimId) {
        log.info("Fetching insurance claim with ID: {}", claimId);
        return insuranceClaimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Insurance claim not found with ID: " + claimId));
    }

    @Override
    public InsuranceClaimResponseDto getClaimResponseById(Long claimId) {
        log.info("Fetching insurance claim response for ID: {}", claimId);
        InsuranceClaim claim = getClaimById(claimId);

        return InsuranceClaimResponseDto.builder()
                .id(claim.getId())
                .billingId(claim.getBilling().getId())
                .policyId(claim.getPolicy().getId())
                .policyNumber(claim.getPolicy().getPolicyNumber())
                .provider(claim.getPolicy().getProvider())
                .claimAmount(claim.getClaimAmount())
                .status(claim.getStatus().name())
                .submittedDt(claim.getSubmittedDt())
                .settledDt(claim.getSettledDt())
                .build();
    }

    @Override
    @Transactional
    public InsuranceClaim updateClaimStatus(Long claimId, InsuranceClaim.ClaimStatus status, Long updatedBy) {
        log.info("Updating claim ID: {} to status: {}", claimId, status);

        InsuranceClaim claim = getClaimById(claimId);
        claim.setStatus(status);
        claim.setUpdatedBy(updatedBy);

        if (status == InsuranceClaim.ClaimStatus.SETTLED) {
            claim.setSettledDt(LocalDateTime.now());
        }

        claim = insuranceClaimRepository.save(claim);
        log.info("Claim status updated to: {}", status);

        return claim;
    }

    @Override
    @Transactional
    public InsuranceSettlement createSettlement(InsuranceSettlementRequestDto request, Long createdBy) {
        log.info("Creating settlement for claim ID: {}", request.getClaimId());

        InsuranceClaim claim = getClaimById(request.getClaimId());

        // Validate settlement amount
        if (request.getAmountSettled().compareTo(claim.getClaimAmount()) > 0) {
            throw new RuntimeException("Settlement amount cannot exceed claim amount");
        }

        InsuranceSettlement settlement = new InsuranceSettlement();
        settlement.setClaim(claim);
        settlement.setAmountSettled(request.getAmountSettled());
        settlement.setRemarks(request.getRemarks());
        settlement.setCreatedBy(createdBy);
        settlement.setUpdatedBy(createdBy);

        settlement = insuranceSettlementRepository.save(settlement);

        // Update claim status to settled
        claim.setStatus(InsuranceClaim.ClaimStatus.SETTLED);
        claim.setSettledDt(LocalDateTime.now());
        claim.setUpdatedBy(createdBy);
        insuranceClaimRepository.save(claim);

        // Update billing paid amount
        Billing billing = claim.getBilling();
        billing.setPaidAmount(billing.getPaidAmount().add(request.getAmountSettled()));
        billing.setOutstandingAmount(billing.getNetAmount().subtract(billing.getPaidAmount()));

        if (billing.getOutstandingAmount().compareTo(BigDecimal.ZERO) == 0) {
            billing.setStatus(Billing.BillingStatus.PAID);
        } else if (billing.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            billing.setStatus(Billing.BillingStatus.PARTIALLY_PAID);
        }

        billing.setUpdatedBy(createdBy);
        billingRepository.save(billing);

        log.info("Settlement created with ID: {}", settlement.getId());
        return settlement;
    }

    @Override
    public List<InsuranceSettlement> getSettlementsByClaimId(Long claimId) {
        log.info("Fetching settlements for claim ID: {}", claimId);
        return insuranceSettlementRepository.findByClaimId(claimId);
    }

    @Override
    public Page<InsuranceClaim> getClaimsByStatus(InsuranceClaim.ClaimStatus status, Pageable pageable) {
        log.info("Fetching claims with status: {}", status);
        return insuranceClaimRepository.findByStatus(status, pageable);
    }

    @Override
    public List<InsuranceClaim> getClaimsByPatientId(Long patientId) {
        log.info("Fetching claims for patient ID: {}", patientId);
        return insuranceClaimRepository.findByPatientId(patientId);
    }

    @Override
    public List<InsuranceClaim> getClaimsByPolicyId(Long policyId) {
        log.info("Fetching claims for policy ID: {}", policyId);
        return insuranceClaimRepository.findByPolicyId(policyId);
    }
}

