package com.mhms.medisynapse.dto;

import com.mhms.medisynapse.entity.Appointment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequestDto {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Hospital ID is required")
    private Long hospitalId;

    private Long departmentId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @Builder.Default
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes = 30;

    @Builder.Default
    private Appointment.AppointmentType appointmentType = Appointment.AppointmentType.CONSULTATION;

    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String reason;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @Builder.Default
    private Boolean isRecurring = false;

    private String recurringPattern;
}
