package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillItemDto {
    private Long id;
    private Long billingId;
    private String serviceType;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;
}

