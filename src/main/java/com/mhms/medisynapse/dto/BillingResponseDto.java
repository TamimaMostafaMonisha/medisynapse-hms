package com.mhms.medisynapse.dto;

import com.mhms.medisynapse.entity.Billing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingResponseDto {
    private Long id;
    private String billNumber;
    private LocalDate billDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
    private BigDecimal paidAmount;
    private BigDecimal outstandingAmount;
    private String paymentMethod;
    private String status;
    private String notes;

    // Simplified patient info
    private Long patientId;
    private String patientName;
    private String patientContact;

    // Simplified hospital info
    private Long hospitalId;
    private String hospitalName;

    // Simplified appointment info
    private Long appointmentId;
    private String appointmentDate;

    private LocalDateTime createdDt;
    private LocalDateTime lastUpdatedDt;

    public static BillingResponseDto fromEntity(Billing billing) {
        BillingResponseDto dto = new BillingResponseDto();
        dto.setId(billing.getId());
        dto.setBillNumber(billing.getBillNumber());
        dto.setBillDate(billing.getBillDate());
        dto.setDueDate(billing.getDueDate());
        dto.setTotalAmount(billing.getTotalAmount());
        dto.setDiscountAmount(billing.getDiscountAmount());
        dto.setTaxAmount(billing.getTaxAmount());
        dto.setNetAmount(billing.getNetAmount());
        dto.setPaidAmount(billing.getPaidAmount());
        dto.setOutstandingAmount(billing.getOutstandingAmount());
        dto.setPaymentMethod(billing.getPaymentMethod() != null ? billing.getPaymentMethod().toString() : null);
        dto.setStatus(billing.getStatus() != null ? billing.getStatus().toString() : null);
        dto.setNotes(billing.getNotes());

        // Set patient info safely
        if (billing.getPatient() != null) {
            dto.setPatientId(billing.getPatient().getId());
            dto.setPatientName(billing.getPatient().getFirstName() + " " + billing.getPatient().getLastName());
            dto.setPatientContact(billing.getPatient().getContact());
        }

        // Set hospital info safely
        if (billing.getHospital() != null) {
            dto.setHospitalId(billing.getHospital().getId());
            dto.setHospitalName(billing.getHospital().getName());
        }

        // Set appointment info safely
        if (billing.getAppointment() != null) {
            dto.setAppointmentId(billing.getAppointment().getId());
            dto.setAppointmentDate(billing.getAppointment().getStartTime() != null ?
                    billing.getAppointment().getStartTime().toString() : null);
        }

        dto.setCreatedDt(billing.getCreatedDt());
        dto.setLastUpdatedDt(billing.getLastUpdatedDt());

        return dto;
    }
}

