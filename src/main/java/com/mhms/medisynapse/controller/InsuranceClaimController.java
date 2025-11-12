package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.InsuranceClaimRequestDto;
import com.mhms.medisynapse.dto.InsuranceClaimResponseDto;
import com.mhms.medisynapse.dto.InsuranceSettlementRequestDto;
import com.mhms.medisynapse.entity.InsuranceClaim;
import com.mhms.medisynapse.entity.InsuranceSettlement;
import com.mhms.medisynapse.service.InsuranceClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/insurance/claims")
@RequiredArgsConstructor
@Slf4j
public class InsuranceClaimController {

    private final InsuranceClaimService insuranceClaimService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InsuranceClaim>> createClaim(
            @Valid @RequestBody InsuranceClaimRequestDto request,
            @RequestParam Long createdBy) {

        log.info("Creating insurance claim for billing ID: {}", request.getBillingId());

        InsuranceClaim claim = insuranceClaimService.createClaim(request, createdBy);

        log.info("Successfully created claim with ID: {}", claim.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Insurance claim created successfully", claim));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InsuranceClaimResponseDto>> getClaimById(@PathVariable Long id) {

        log.info("Fetching insurance claim with ID: {}", id);

        InsuranceClaimResponseDto claim = insuranceClaimService.getClaimResponseById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Insurance claim retrieved successfully", claim)
        );
    }

    @PutMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InsuranceClaim>> updateClaimStatus(
            @PathVariable Long id,
            @RequestParam InsuranceClaim.ClaimStatus status,
            @RequestParam Long updatedBy) {

        log.info("Updating claim ID: {} to status: {}", id, status);

        InsuranceClaim claim = insuranceClaimService.updateClaimStatus(id, status, updatedBy);

        log.info("Successfully updated claim status to: {}", status);

        return ResponseEntity.ok(
                ApiResponse.success("Claim status updated successfully", claim)
        );
    }

    @PostMapping(value = "/settlements", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InsuranceSettlement>> createSettlement(
            @Valid @RequestBody InsuranceSettlementRequestDto request,
            @RequestParam Long createdBy) {

        log.info("Creating settlement for claim ID: {}", request.getClaimId());

        InsuranceSettlement settlement = insuranceClaimService.createSettlement(request, createdBy);

        log.info("Successfully created settlement with ID: {}", settlement.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Settlement created successfully", settlement));
    }

    @GetMapping(value = "/{id}/settlements", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<InsuranceSettlement>>> getSettlements(@PathVariable Long id) {

        log.info("Fetching settlements for claim ID: {}", id);

        List<InsuranceSettlement> settlements = insuranceClaimService.getSettlementsByClaimId(id);

        return ResponseEntity.ok(
                ApiResponse.success("Settlements retrieved successfully", settlements)
        );
    }

    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<InsuranceClaim>>> getClaimsByStatus(
            @PathVariable InsuranceClaim.ClaimStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("Fetching claims with status: {}", status);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedDt"));
        Page<InsuranceClaim> claims = insuranceClaimService.getClaimsByStatus(status, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Claims retrieved successfully", claims)
        );
    }

    @GetMapping(value = "/patient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<InsuranceClaim>>> getPatientClaims(@PathVariable Long patientId) {

        log.info("Fetching claims for patient ID: {}", patientId);

        List<InsuranceClaim> claims = insuranceClaimService.getClaimsByPatientId(patientId);

        return ResponseEntity.ok(
                ApiResponse.success("Patient claims retrieved successfully", claims)
        );
    }

    @GetMapping(value = "/policy/{policyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<InsuranceClaim>>> getPolicyClaims(@PathVariable Long policyId) {

        log.info("Fetching claims for policy ID: {}", policyId);

        List<InsuranceClaim> claims = insuranceClaimService.getClaimsByPolicyId(policyId);

        return ResponseEntity.ok(
                ApiResponse.success("Policy claims retrieved successfully", claims)
        );
    }
}

