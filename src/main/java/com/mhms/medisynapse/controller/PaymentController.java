package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.PaymentRequestDto;
import com.mhms.medisynapse.dto.PaymentResponseDto;
import com.mhms.medisynapse.service.PaymentService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Process a new payment for a billing record
     * POST /api/v1/payments
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PaymentResponseDto>> processPayment(
            @Valid @RequestBody PaymentRequestDto request,
            @RequestParam Long createdBy) {

        log.info("Processing payment for billing ID: {}", request.getBillingId());

        PaymentResponseDto payment = paymentService.processPayment(request, createdBy);

        log.info("Successfully processed payment with ID: {}", payment.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment processed successfully", payment));
    }

    /**
     * Get payment by ID
     * GET /api/v1/payments/{id}
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getPaymentById(@PathVariable Long id) {

        log.info("Fetching payment with ID: {}", id);

        PaymentResponseDto payment = paymentService.getPaymentById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Payment retrieved successfully", payment)
        );
    }

    /**
     * Get all payments for a specific billing
     * GET /api/v1/payments/billing/{billingId}
     */
    @GetMapping(value = "/billing/{billingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<PaymentResponseDto>>> getPaymentsByBillingId(
            @PathVariable Long billingId) {

        log.info("Fetching payments for billing ID: {}", billingId);

        List<PaymentResponseDto> payments = paymentService.getPaymentsByBillingId(billingId);

        return ResponseEntity.ok(
                ApiResponse.success("Payments retrieved successfully", payments)
        );
    }

    /**
     * Get paginated payments for a specific billing
     * GET /api/v1/payments/billing/{billingId}/paginated
     */
    @GetMapping(value = "/billing/{billingId}/paginated", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<PaymentResponseDto>>> getPaymentsByBillingIdPaginated(
            @PathVariable Long billingId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("Fetching paginated payments for billing ID: {}", billingId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));
        Page<PaymentResponseDto> payments = paymentService.getPaymentsByBillingIdPaginated(billingId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Payments retrieved successfully", payments)
        );
    }

    /**
     * Get all payments for a specific patient
     * GET /api/v1/payments/patient/{patientId}
     */
    @GetMapping(value = "/patient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<PaymentResponseDto>>> getPaymentsByPatientId(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("Fetching payments for patient ID: {}", patientId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));
        Page<PaymentResponseDto> payments = paymentService.getPaymentsByPatientId(patientId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Patient payments retrieved successfully", payments)
        );
    }

    /**
     * Get all payments for a specific hospital
     * GET /api/v1/payments/hospital/{hospitalId}
     */
    @GetMapping(value = "/hospital/{hospitalId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<PaymentResponseDto>>> getPaymentsByHospitalId(
            @PathVariable Long hospitalId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("Fetching payments for hospital ID: {}", hospitalId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));
        Page<PaymentResponseDto> payments = paymentService.getPaymentsByHospitalId(hospitalId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Hospital payments retrieved successfully", payments)
        );
    }

    /**
     * Void/cancel a payment
     * DELETE /api/v1/payments/{id}
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PaymentResponseDto>> voidPayment(
            @PathVariable Long id,
            @RequestParam Long updatedBy) {

        log.info("Voiding payment with ID: {}", id);

        PaymentResponseDto payment = paymentService.voidPayment(id, updatedBy);

        log.info("Successfully voided payment with ID: {}", id);

        return ResponseEntity.ok(
                ApiResponse.success("Payment voided successfully", payment)
        );
    }
}

