package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatisticsDto {
    private Long totalAppointments;
    private Long scheduledAppointments;
    private Long confirmedAppointments;
    private Long completedAppointments;
    private Long cancelledAppointments;
    private Long noShowAppointments;
    private Long inProgressAppointments;
    private Long rescheduledAppointments;

    // Department-wise statistics
    private Map<String, Long> appointmentsByDepartment;

    // Doctor-wise statistics
    private Map<String, Long> appointmentsByDoctor;

    // Appointment type statistics
    private Map<String, Long> appointmentsByType;

    // Daily statistics
    private Map<String, Long> appointmentsByDay;

    // Average appointment duration
    private Double averageDurationMinutes;

    // Patient statistics
    private Long totalPatients;
    private Long newPatients;
    private Long returningPatients;
}
