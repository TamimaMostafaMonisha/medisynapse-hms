package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.PaymentRequestDto;
import com.mhms.medisynapse.dto.PaymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {

    /**
     * Process a payment for a billing record
     * Automatically updates billing status based on payment amount
     */
    PaymentResponseDto processPayment(PaymentRequestDto request, Long createdBy);

    /**
     * Get payment by ID
     */
    PaymentResponseDto getPaymentById(Long paymentId);

    /**
     * Get all payments for a specific billing
     */
    List<PaymentResponseDto> getPaymentsByBillingId(Long billingId);

    /**
     * Get paginated payments for a specific billing
     */
    Page<PaymentResponseDto> getPaymentsByBillingIdPaginated(Long billingId, Pageable pageable);

    /**
     * Get all payments for a specific patient
     */
    Page<PaymentResponseDto> getPaymentsByPatientId(Long patientId, Pageable pageable);

    /**
     * Get all payments for a specific hospital
     */
    Page<PaymentResponseDto> getPaymentsByHospitalId(Long hospitalId, Pageable pageable);

    /**
     * Void/cancel a payment
     */
    PaymentResponseDto voidPayment(Long paymentId, Long updatedBy);
}

