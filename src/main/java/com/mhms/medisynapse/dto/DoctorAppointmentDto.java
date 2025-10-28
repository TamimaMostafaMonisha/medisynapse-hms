package com.mhms.medisynapse.dto;

import com.mhms.medisynapse.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAppointmentDto {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Long hospitalId;
    private String appointmentDate;  // YYYY-MM-DD format
    private String appointmentTime;  // HH:mm format
    private Integer duration;
    private Appointment.AppointmentType appointmentType;
    private Appointment.AppointmentStatus status;
    private String reason;
    private String notes;
    private LocalDateTime createdAt;
}

