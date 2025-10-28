package com.mhms.medisynapse.dto;

import com.mhms.medisynapse.entity.Appointment;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequestDto {

    private LocalDateTime startTime;

    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    private Appointment.AppointmentType appointmentType;

    private Appointment.AppointmentStatus status;

    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String reason;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @Size(max = 500, message = "Cancellation reason cannot exceed 500 characters")
    private String cancellationReason;

    private Boolean isRecurring;

    private String recurringPattern;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;
}
