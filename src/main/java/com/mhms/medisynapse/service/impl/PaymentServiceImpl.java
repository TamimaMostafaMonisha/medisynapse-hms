package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.PaymentRequestDto;
import com.mhms.medisynapse.dto.PaymentResponseDto;
import com.mhms.medisynapse.entity.Billing;
import com.mhms.medisynapse.entity.Payment;
import com.mhms.medisynapse.exception.ResourceNotFoundException;
import com.mhms.medisynapse.repository.BillingRepository;
import com.mhms.medisynapse.repository.PaymentRepository;
import com.mhms.medisynapse.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillingRepository billingRepository;

    @Override
    @Transactional
    public PaymentResponseDto processPayment(PaymentRequestDto request, Long createdBy) {
        log.info("Processing payment for billing ID: {}", request.getBillingId());

        // Fetch billing record
        Billing billing = billingRepository.findById(request.getBillingId())
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found with ID: " + request.getBillingId()));

        // Validate payment amount
        if (request.getAmount().compareTo(billing.getOutstandingAmount()) > 0) {
            throw new IllegalArgumentException("Payment amount cannot exceed outstanding amount: " + billing.getOutstandingAmount());
        }

        // Create a payment record
        Payment payment = new Payment();
        payment.setBilling(billing);
        payment.setPatient(billing.getPatient());
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setReferenceNo(request.getReferenceNo());
        payment.setCreatedBy(createdBy);
        payment.setIsActive(true);

        payment = paymentRepository.save(payment);
        log.info("Payment saved with ID: {}", payment.getId());

        // Update billing amounts and status
        updateBillingStatus(billing, request.getAmount());

        return mapToResponseDto(payment);
    }

    private void updateBillingStatus(Billing billing, BigDecimal paymentAmount) {
        // Update paid amount
        BigDecimal newPaidAmount = billing.getPaidAmount().add(paymentAmount);
        billing.setPaidAmount(newPaidAmount);

        // Update outstanding amount
        BigDecimal newOutstandingAmount = billing.getNetAmount().subtract(newPaidAmount);
        billing.setOutstandingAmount(newOutstandingAmount);

        // Update billing status based on outstanding amount
        if (newOutstandingAmount.compareTo(BigDecimal.ZERO) == 0) {
            billing.setStatus(Billing.BillingStatus.PAID);
            log.info("Billing ID {} status updated to PAID", billing.getId());
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            billing.setStatus(Billing.BillingStatus.PARTIALLY_PAID);
            log.info("Billing ID {} status updated to PARTIALLY_PAID", billing.getId());
        }

        billingRepository.save(billing);
        log.info("Billing ID {} updated - Paid: {}, Outstanding: {}",
                billing.getId(), newPaidAmount, newOutstandingAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(Long paymentId) {
        log.info("Fetching payment with ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        return mapToResponseDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByBillingId(Long billingId) {
        log.info("Fetching payments for billing ID: {}", billingId);

        List<Payment> payments = paymentRepository.findByBillingIdAndIsActiveTrue(billingId);

        return payments.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDto> getPaymentsByBillingIdPaginated(Long billingId, Pageable pageable) {
        log.info("Fetching paginated payments for billing ID: {}", billingId);

        Page<Payment> payments = paymentRepository.findByBillingIdAndIsActiveTrue(billingId, pageable);

        return payments.map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDto> getPaymentsByPatientId(Long patientId, Pageable pageable) {
        log.info("Fetching payments for patient ID: {}", patientId);

        Page<Payment> payments = paymentRepository.findByPatientId(patientId, pageable);

        return payments.map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDto> getPaymentsByHospitalId(Long hospitalId, Pageable pageable) {
        log.info("Fetching payments for hospital ID: {}", hospitalId);

        Page<Payment> payments = paymentRepository.findByHospitalId(hospitalId, pageable);

        return payments.map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public PaymentResponseDto voidPayment(Long paymentId, Long updatedBy) {
        log.info("Voiding payment with ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        if (!payment.getIsActive()) {
            throw new IllegalStateException("Payment is already voided");
        }

        // Mark payment as inactive
        payment.setIsActive(false);
        payment.setUpdatedBy(updatedBy);
        payment = paymentRepository.save(payment);

        // Reverse the payment in billing
        Billing billing = payment.getBilling();
        BigDecimal reversedPaidAmount = billing.getPaidAmount().subtract(payment.getAmount());
        BigDecimal reversedOutstandingAmount = billing.getOutstandingAmount().add(payment.getAmount());

        billing.setPaidAmount(reversedPaidAmount);
        billing.setOutstandingAmount(reversedOutstandingAmount);

        // Update billing status
        if (reversedOutstandingAmount.compareTo(billing.getNetAmount()) == 0) {
            billing.setStatus(Billing.BillingStatus.DRAFT);
        } else if (reversedPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            billing.setStatus(Billing.BillingStatus.PARTIALLY_PAID);
        }

        billingRepository.save(billing);
        log.info("Payment ID {} voided and billing ID {} updated", paymentId, billing.getId());

        return mapToResponseDto(payment);
    }

    private PaymentResponseDto mapToResponseDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .billingId(payment.getBilling().getId())
                .billNumber(payment.getBilling().getBillNumber())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .referenceNo(payment.getReferenceNo())
                .createdDt(payment.getCreatedDt())
                .createdBy(payment.getCreatedBy())
                .isActive(payment.getIsActive())
                .build();
    }
}
