package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.BillItemDto;
import com.mhms.medisynapse.dto.BillingResponseDto;
import com.mhms.medisynapse.dto.CreateBillRequestDto;
import com.mhms.medisynapse.dto.RefundRequestDto;
import com.mhms.medisynapse.entity.Appointment;
import com.mhms.medisynapse.entity.BillItem;
import com.mhms.medisynapse.entity.Billing;
import com.mhms.medisynapse.entity.Hospital;
import com.mhms.medisynapse.entity.Patient;
import com.mhms.medisynapse.entity.Refund;
import com.mhms.medisynapse.repository.AppointmentRepository;
import com.mhms.medisynapse.repository.BillItemRepository;
import com.mhms.medisynapse.repository.BillingRepository;
import com.mhms.medisynapse.repository.HospitalRepository;
import com.mhms.medisynapse.repository.PatientRepository;
import com.mhms.medisynapse.repository.RefundRepository;
import com.mhms.medisynapse.service.BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BillingServiceImpl implements BillingService {

    private final BillingRepository billingRepository;
    private final BillItemRepository billItemRepository;
    private final RefundRepository refundRepository;
    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public BillingResponseDto createBill(CreateBillRequestDto request, Long createdBy) {
        log.info("Creating bill for patient ID: {}", request.getPatientId());

        // Validate patient exists
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + request.getPatientId()));

        // Validate hospital exists
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found with ID: " + request.getHospitalId()));

        // Validate appointment if provided
        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + request.getAppointmentId()));
        }

        // Calculate total from items
        BigDecimal itemsTotal = request.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate net amount
        BigDecimal discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal taxAmount = request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal netAmount = itemsTotal.subtract(discountAmount).add(taxAmount);

        // Create billing
        Billing billing = new Billing();
        billing.setPatient(patient);
        billing.setHospital(hospital);
        billing.setAppointment(appointment);
        billing.setBillNumber(generateBillNumber());
        billing.setBillDate(LocalDate.now());
        billing.setTotalAmount(itemsTotal);
        billing.setDiscountAmount(discountAmount);
        billing.setTaxAmount(taxAmount);
        billing.setNetAmount(netAmount);
        billing.setPaidAmount(BigDecimal.ZERO);
        billing.setOutstandingAmount(netAmount);
        billing.setStatus(Billing.BillingStatus.DRAFT);
        billing.setNotes(request.getNotes());
        billing.setCreatedBy(createdBy);
        billing.setUpdatedBy(createdBy);

        billing = billingRepository.save(billing);
        log.info("Billing created with ID: {}", billing.getId());

        // Create bill items
        for (BillItemDto itemDto : request.getItems()) {
            BillItem billItem = new BillItem();
            billItem.setBilling(billing);
            billItem.setServiceType(itemDto.getServiceType());
            billItem.setDescription(itemDto.getDescription());
            billItem.setQuantity(itemDto.getQuantity());
            billItem.setUnitPrice(itemDto.getUnitPrice());
            billItem.setTotal(itemDto.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            billItem.setCreatedBy(createdBy);
            billItem.setUpdatedBy(createdBy);
            billItemRepository.save(billItem);
        }

        log.info("Created {} bill items for billing ID: {}", request.getItems().size(), billing.getId());
        return BillingResponseDto.fromEntity(billing);
    }

    @Override
    @Transactional
    public BillingResponseDto updateBill(Long billingId, CreateBillRequestDto request, Long updatedBy) {
        log.info("Updating bill ID: {}", billingId);

        // Fetch existing billing
        Billing billing = getBillingEntityById(billingId);

        // Check if billing can be updated (only DRAFT or PARTIALLY_PAID bills can be updated)
        if (billing.getStatus() == Billing.BillingStatus.PAID ||
            billing.getStatus() == Billing.BillingStatus.REFUNDED ||
            billing.getStatus() == Billing.BillingStatus.CANCELLED) {
            throw new RuntimeException("Cannot update billing with status: " + billing.getStatus());
        }

        // Validate patient exists
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + request.getPatientId()));

        // Validate hospital exists
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found with ID: " + request.getHospitalId()));

        // Validate appointment if provided
        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + request.getAppointmentId()));
        }

        // Delete existing bill items
        List<BillItem> existingItems = billItemRepository.findByBillingId(billingId);
        billItemRepository.deleteAll(existingItems);
        log.info("Deleted {} existing bill items for billing ID: {}", existingItems.size(), billingId);

        // Calculate total from new items
        BigDecimal itemsTotal = request.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate net amount
        BigDecimal discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal taxAmount = request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal netAmount = itemsTotal.subtract(discountAmount).add(taxAmount);

        // Update billing
        billing.setPatient(patient);
        billing.setHospital(hospital);
        billing.setAppointment(appointment);
        billing.setTotalAmount(itemsTotal);
        billing.setDiscountAmount(discountAmount);
        billing.setTaxAmount(taxAmount);
        billing.setNetAmount(netAmount);
        billing.setOutstandingAmount(netAmount.subtract(billing.getPaidAmount()));
        billing.setNotes(request.getNotes());
        billing.setUpdatedBy(updatedBy);

        billing = billingRepository.save(billing);
        log.info("Billing updated with ID: {}", billing.getId());

        // Create new bill items
        for (BillItemDto itemDto : request.getItems()) {
            BillItem billItem = new BillItem();
            billItem.setBilling(billing);
            billItem.setServiceType(itemDto.getServiceType());
            billItem.setDescription(itemDto.getDescription());
            billItem.setQuantity(itemDto.getQuantity());
            billItem.setUnitPrice(itemDto.getUnitPrice());
            billItem.setTotal(itemDto.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            billItem.setCreatedBy(updatedBy);
            billItem.setUpdatedBy(updatedBy);
            billItemRepository.save(billItem);
        }

        log.info("Created {} new bill items for billing ID: {}", request.getItems().size(), billing.getId());
        return BillingResponseDto.fromEntity(billing);
    }

    @Override
    public BillingResponseDto getBillingById(Long billingId) {
        log.info("Fetching billing with ID: {}", billingId);
        Billing billing = billingRepository.findById(billingId)
                .orElseThrow(() -> new RuntimeException("Billing not found with ID: " + billingId));
        return BillingResponseDto.fromEntity(billing);
    }

    @Override
    public Billing getBillingEntityById(Long billingId) {
        log.info("Fetching billing entity with ID: {}", billingId);
        return billingRepository.findById(billingId)
                .orElseThrow(() -> new RuntimeException("Billing not found with ID: " + billingId));
    }

    @Override
    public List<BillItem> getBillItems(Long billingId) {
        log.info("Fetching bill items for billing ID: {}", billingId);
        return billItemRepository.findByBillingId(billingId);
    }

    @Override
    @Transactional
    public BillItem addBillItem(Long billingId, BillItemDto itemDto, Long createdBy) {
        log.info("Adding bill item to billing ID: {}", billingId);

        Billing billing = getBillingEntityById(billingId);

        BillItem billItem = new BillItem();
        billItem.setBilling(billing);
        billItem.setServiceType(itemDto.getServiceType());
        billItem.setDescription(itemDto.getDescription());
        billItem.setQuantity(itemDto.getQuantity());
        billItem.setUnitPrice(itemDto.getUnitPrice());
        billItem.setTotal(itemDto.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        billItem.setCreatedBy(createdBy);
        billItem.setUpdatedBy(createdBy);

        billItem = billItemRepository.save(billItem);

        // Update billing total
        BigDecimal newTotal = billing.getTotalAmount().add(billItem.getTotal());
        billing.setTotalAmount(newTotal);
        billing.setNetAmount(newTotal.subtract(billing.getDiscountAmount()).add(billing.getTaxAmount()));
        billing.setOutstandingAmount(billing.getNetAmount().subtract(billing.getPaidAmount()));
        billing.setUpdatedBy(createdBy);
        billingRepository.save(billing);

        log.info("Bill item added with ID: {}", billItem.getId());
        return billItem;
    }

    @Override
    @Transactional
    public Refund processRefund(RefundRequestDto request, Long createdBy) {
        log.info("Processing refund for billing ID: {}", request.getBillingId());

        Billing billing = getBillingEntityById(request.getBillingId());

        // Validate refund amount
        if (request.getAmount().compareTo(billing.getPaidAmount()) > 0) {
            throw new RuntimeException("Refund amount cannot exceed paid amount");
        }

        Refund refund = new Refund();
        refund.setBilling(billing);
        refund.setAmount(request.getAmount());
        refund.setReason(request.getReason());
        refund.setCreatedBy(createdBy);
        refund.setUpdatedBy(createdBy);

        refund = refundRepository.save(refund);

        // Update billing status
        billing.setPaidAmount(billing.getPaidAmount().subtract(request.getAmount()));
        billing.setOutstandingAmount(billing.getNetAmount().subtract(billing.getPaidAmount()));

        if (billing.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
            billing.setStatus(Billing.BillingStatus.REFUNDED);
        } else if (billing.getOutstandingAmount().compareTo(BigDecimal.ZERO) > 0) {
            billing.setStatus(Billing.BillingStatus.PARTIALLY_PAID);
        }

        billing.setUpdatedBy(createdBy);
        billingRepository.save(billing);

        log.info("Refund processed with ID: {}", refund.getId());
        return refund;
    }

    @Override
    public List<Refund> getRefundsByBillingId(Long billingId) {
        log.info("Fetching refunds for billing ID: {}", billingId);
        return refundRepository.findByBillingId(billingId);
    }

    @Override
    public Page<BillingResponseDto> getBillingsByPatientId(Long patientId, Billing.BillingStatus status, Pageable pageable) {
        log.info("Fetching billings for patient ID: {} with status: {}", patientId, status);

        if (status != null) {
            return billingRepository.findByPatientIdAndStatusAndIsActiveTrue(patientId, status, pageable)
                    .map(BillingResponseDto::fromEntity);
        } else {
            return billingRepository.findByPatientIdAndIsActiveTrue(patientId, pageable)
                    .map(BillingResponseDto::fromEntity);
        }
    }

    @Override
    public Page<BillingResponseDto> getBillingsByHospitalId(Long hospitalId, Billing.BillingStatus status, Pageable pageable) {
        log.info("Fetching billings for hospital ID: {} with status: {}", hospitalId, status);

        if (status != null) {
            return billingRepository.findByHospitalIdAndStatusAndIsActiveTrue(hospitalId, status, pageable)
                    .map(BillingResponseDto::fromEntity);
        } else {
            return billingRepository.findByHospitalIdAndIsActiveTrue(hospitalId, pageable)
                    .map(BillingResponseDto::fromEntity);
        }
    }

    @Override
    public Page<BillingResponseDto> getAllActiveBillings(Pageable pageable) {
        log.info("Fetching all active billings for super admin");
        return billingRepository.findAllActiveBillings(pageable)
                .map(BillingResponseDto::fromEntity);
    }

    private String generateBillNumber() {
        // Generate unique bill number (you can customize this logic)
        return "BILL-" + System.currentTimeMillis();
    }
}
