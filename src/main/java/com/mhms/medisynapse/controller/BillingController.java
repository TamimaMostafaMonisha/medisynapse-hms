package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.BillItemDto;
import com.mhms.medisynapse.dto.BillingResponseDto;
import com.mhms.medisynapse.dto.CreateBillRequestDto;
import com.mhms.medisynapse.dto.RefundRequestDto;
import com.mhms.medisynapse.entity.BillItem;
import com.mhms.medisynapse.entity.Billing;
import com.mhms.medisynapse.entity.Refund;
import com.mhms.medisynapse.service.BillingService;
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
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final BillingService billingService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BillingResponseDto>> createBill(
            @Valid @RequestBody CreateBillRequestDto request,
            @RequestParam Long createdBy) {

        log.info("Creating bill for patient ID: {}", request.getPatientId());

        BillingResponseDto billing = billingService.createBill(request, createdBy);

        log.info("Successfully created bill with ID: {}", billing.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bill created successfully", billing));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BillingResponseDto>> updateBill(
            @PathVariable Long id,
            @Valid @RequestBody CreateBillRequestDto request,
            @RequestParam Long updatedBy) {

        log.info("Updating bill with ID: {}", id);

        BillingResponseDto billing = billingService.updateBill(id, request, updatedBy);

        log.info("Successfully updated bill with ID: {}", billing.getId());

        return ResponseEntity.ok(
                ApiResponse.success("Bill updated successfully", billing));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BillingResponseDto>> getBillingById(@PathVariable Long id) {

        log.info("Fetching bill with ID: {}", id);

        BillingResponseDto billing = billingService.getBillingById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Bill retrieved successfully", billing)
        );
    }

    @GetMapping(value = "/{id}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<BillItem>>> getBillItems(@PathVariable Long id) {

        log.info("Fetching bill items for billing ID: {}", id);

        List<BillItem> items = billingService.getBillItems(id);

        return ResponseEntity.ok(
                ApiResponse.success("Bill items retrieved successfully", items)
        );
    }

    @PostMapping(value = "/{id}/items", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BillItem>> addBillItem(
            @PathVariable Long id,
            @Valid @RequestBody BillItemDto itemDto,
            @RequestParam Long createdBy) {

        log.info("Adding bill item to billing ID: {}", id);

        BillItem billItem = billingService.addBillItem(id, itemDto, createdBy);

        log.info("Successfully added bill item with ID: {}", billItem.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bill item added successfully", billItem));
    }

    @PostMapping(value = "/refund", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Refund>> processRefund(
            @Valid @RequestBody RefundRequestDto request,
            @RequestParam Long createdBy) {

        log.info("Processing refund for billing ID: {}", request.getBillingId());

        Refund refund = billingService.processRefund(request, createdBy);

        log.info("Successfully processed refund with ID: {}", refund.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Refund processed successfully", refund));
    }

    @GetMapping(value = "/{id}/refunds", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Refund>>> getRefunds(@PathVariable Long id) {

        log.info("Fetching refunds for billing ID: {}", id);

        List<Refund> refunds = billingService.getRefundsByBillingId(id);

        return ResponseEntity.ok(
                ApiResponse.success("Refunds retrieved successfully", refunds)
        );
    }

    @GetMapping(value = "/patient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<BillingResponseDto>>> getPatientBillings(
            @PathVariable Long patientId,
            @RequestParam(required = false) Billing.BillingStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("Fetching billings for patient ID: {} with status: {}", patientId, status);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDt"));
        Page<BillingResponseDto> billings = billingService.getBillingsByPatientId(patientId, status, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Patient billings retrieved successfully", billings)
        );
    }

    @GetMapping(value = "/hospital/{hospitalId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<BillingResponseDto>>> getHospitalBillings(
            @PathVariable Long hospitalId,
            @RequestParam(required = false) Billing.BillingStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("Fetching billings for hospital ID: {} with status: {}", hospitalId, status);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDt"));
        Page<BillingResponseDto> billings = billingService.getBillingsByHospitalId(hospitalId, status, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Hospital billings retrieved successfully", billings)
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<BillingResponseDto>>> getAllBillingsDefault(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        log.info("GET /api/v1/billing - Fetching all hospitals' billing information for super admin");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDt"));
        Page<BillingResponseDto> billings = billingService.getAllActiveBillings(pageable);
        return ResponseEntity.ok(
                ApiResponse.success("All hospitals' billings retrieved successfully", billings)
        );
    }
}
