package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDetailsResponseDto {
    private AppointmentDetailDto appointment;
    private PatientDetailDto patient;
    private List<String> medicalHistory;
    private List<PreviousAppointmentDto> previousAppointments;
    private List<PrescriptionDto> prescriptions;
    private VitalSignsDto vitalSigns;
    private List<LabTestOrderResponse> labTestOrders;  // NEW: Lab test orders with results
}


