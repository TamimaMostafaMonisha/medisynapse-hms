package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.*;
import com.mhms.medisynapse.entity.Appointment;
import com.mhms.medisynapse.entity.Patient;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface DoctorService {

    /**
     * Get dashboard statistics for a doctor
     */
    DoctorDashboardStatisticsDto getDashboardStatistics(Long doctorId);

    /**
     * Get paginated list of patients for a doctor
     */
    DoctorPatientsResponseDto getPatients(Long doctorId, Patient.PatientStatus status,
                                         String search, Pageable pageable);

    /**
     * Get paginated list of appointments for a doctor with filters
     */
    DoctorAppointmentsResponseDto getAppointments(Long doctorId, Appointment.AppointmentStatus status,
                                                  LocalDate date, LocalDate startDate,
                                                  LocalDate endDate, Pageable pageable);

    /**
     * Get today's appointments for a doctor
     */
    List<DoctorAppointmentDto> getTodayAppointments(Long doctorId);

    /**
     * Get upcoming appointments (next N days excluding today)
     */
    List<DoctorAppointmentDto> getUpcomingAppointments(Long doctorId, Integer days);

    /**
     * Update appointment status
     */
    DoctorAppointmentDto updateAppointmentStatus(Long appointmentId, Long doctorId,
                                                UpdateAppointmentStatusRequest request);

    /**
     * Get recent patients (last 5 or specified limit)
     */
    List<DoctorPatientDto> getRecentPatients(Long doctorId, Integer limit);

    /**
     * Get detailed appointment information including patient details, medical history, prescriptions, etc.
     */
    AppointmentDetailsResponseDto getAppointmentDetails(Long appointmentId, Long doctorId);

    /**
     * Mark an appointment as completed
     */
    DoctorAppointmentDto completeAppointment(Long appointmentId, Long doctorId);

    /**
     * Get doctor profile information
     */
    DoctorProfileResponseDto getDoctorProfile(Long doctorId);
}
