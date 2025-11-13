package com.mhms.medisynapse.dto;

import com.mhms.medisynapse.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {

    private Long id;
    private Long billingId;
    private String billNumber;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private Payment.PaymentMethod paymentMethod;
    private String referenceNo;
    private LocalDateTime createdDt;
    private Long createdBy;
    private Boolean isActive;
}

