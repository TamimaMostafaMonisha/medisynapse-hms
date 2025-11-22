package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.BillItemDto;
import com.mhms.medisynapse.dto.BillingResponseDto;
import com.mhms.medisynapse.dto.CreateBillRequestDto;
import com.mhms.medisynapse.dto.RefundRequestDto;
import com.mhms.medisynapse.entity.BillItem;
import com.mhms.medisynapse.entity.Billing;
import com.mhms.medisynapse.entity.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BillingService {

    BillingResponseDto createBill(CreateBillRequestDto request, Long createdBy);

    BillingResponseDto updateBill(Long billingId, CreateBillRequestDto request, Long updatedBy);

    BillingResponseDto getBillingById(Long billingId);

    Billing getBillingEntityById(Long billingId); // For internal use

    List<BillItem> getBillItems(Long billingId);

    BillItem addBillItem(Long billingId, BillItemDto itemDto, Long createdBy);

    Refund processRefund(RefundRequestDto request, Long createdBy);

    List<Refund> getRefundsByBillingId(Long billingId);

    Page<BillingResponseDto> getBillingsByPatientId(Long patientId, Billing.BillingStatus status, Pageable pageable);

    Page<BillingResponseDto> getBillingsByHospitalId(Long hospitalId, Billing.BillingStatus status, Pageable pageable);

    Page<BillingResponseDto> getAllActiveBillings(Pageable pageable);
}
