package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBillRequestDto {
    private Long patientId;
    private Long hospitalId;
    private Long appointmentId;
    private List<BillItemDto> items;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private String notes;
}

