package com.mhms.medisynapse.dto;

import com.mhms.medisynapse.entity.Appointment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentStatusRequest {

    @NotNull(message = "Status is required")
    private Appointment.AppointmentStatus status;

    private String notes;
}

