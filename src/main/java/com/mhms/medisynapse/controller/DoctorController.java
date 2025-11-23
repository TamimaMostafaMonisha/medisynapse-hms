package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.*;
import com.mhms.medisynapse.entity.Appointment;
import com.mhms.medisynapse.entity.Patient;
import com.mhms.medisynapse.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Doctor", description = "Doctor management APIs")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/dashboard/statistics")
    @Operation(summary = "Get doctor dashboard statistics",
               description = "Get summary statistics for the doctor's dashboard including patient counts and appointment metrics")
    public ResponseEntity<ApiResponse<DoctorDashboardStatisticsDto>> getDashboardStatistics(
            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId) {

        log.info("GET /api/v1/doctor/dashboard/statistics - doctorId: {}", doctorId);

        DoctorDashboardStatisticsDto statistics = doctorService.getDashboardStatistics(doctorId);

        return ResponseEntity.ok(ApiResponse.<DoctorDashboardStatisticsDto>builder()
                .success(true)
                .message("Dashboard statistics retrieved successfully")
                .data(statistics)
                .build());
    }

    @GetMapping("/patients")
    @Operation(summary = "Get doctor's patients",
               description = "Get paginated list of patients assigned to the doctor with optional filtering")
    public ResponseEntity<ApiResponse<DoctorPatientsResponseDto>> getPatients(
            @Parameter(description = "Doctor ID", required = false)
            @RequestParam(required = false) Long doctorId,

            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Search by patient name, phone, or email")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by patient status (ACTIVE, INACTIVE, SUSPENDED)")
            @RequestParam(required = false) Patient.PatientStatus status) {

        log.info("GET /api/v1/doctor/patients - doctorId: {}, page: {}, size: {}, search: {}, status: {}",
                doctorId, page, size, search, status);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastUpdatedDt"));
        DoctorPatientsResponseDto patients = doctorService.getPatients(doctorId, status, search, pageable);

        return ResponseEntity.ok(ApiResponse.<DoctorPatientsResponseDto>builder()
                .success(true)
                .message("Patients retrieved successfully")
                .data(patients)
                .build());
    }

    @GetMapping("/appointments")
    @Operation(summary = "Get doctor's appointments",
               description = "Get paginated list of appointments for the doctor with optional filtering by status and date range")
    public ResponseEntity<ApiResponse<DoctorAppointmentsResponseDto>> getAppointments(
            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Filter by specific date (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            @Parameter(description = "Filter by status (SCHEDULED, COMPLETED, CANCELLED, NO_SHOW)")
            @RequestParam(required = false) Appointment.AppointmentStatus status,

            @Parameter(description = "Filter from this date (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Filter until this date (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/v1/doctor/appointments - doctorId: {}, page: {}, size: {}, date: {}, status: {}, startDate: {}, endDate: {}",
                doctorId, page, size, date, status, startDate, endDate);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        DoctorAppointmentsResponseDto appointments = doctorService.getAppointments(
                doctorId, status, date, startDate, endDate, pageable);

        return ResponseEntity.ok(ApiResponse.<DoctorAppointmentsResponseDto>builder()
                .success(true)
                .message("Appointments retrieved successfully")
                .data(appointments)
                .build());
    }

    @GetMapping("/appointments/today")
    @Operation(summary = "Get today's appointments",
               description = "Get all appointments scheduled for today for the doctor")
    public ResponseEntity<ApiResponse<List<DoctorAppointmentDto>>> getTodayAppointments(
            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId) {

        log.info("GET /api/v1/doctor/appointments/today - doctorId: {}", doctorId);

        List<DoctorAppointmentDto> appointments = doctorService.getTodayAppointments(doctorId);

        return ResponseEntity.ok(ApiResponse.<List<DoctorAppointmentDto>>builder()
                .success(true)
                .message("Today's appointments retrieved successfully")
                .data(appointments)
                .build());
    }

    @GetMapping("/appointments/upcoming")
    @Operation(summary = "Get upcoming appointments",
               description = "Get appointments scheduled for the next N days (excluding today)")
    public ResponseEntity<ApiResponse<List<DoctorAppointmentDto>>> getUpcomingAppointments(
            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Number of days to look ahead (default: 7)")
            @RequestParam(defaultValue = "7") Integer days) {

        log.info("GET /api/v1/doctor/appointments/upcoming - doctorId: {}, days: {}", doctorId, days);

        List<DoctorAppointmentDto> appointments = doctorService.getUpcomingAppointments(doctorId, days);

        return ResponseEntity.ok(ApiResponse.<List<DoctorAppointmentDto>>builder()
                .success(true)
                .message("Upcoming appointments retrieved successfully")
                .data(appointments)
                .build());
    }

    @PutMapping("/appointments/{appointmentId}/status")
    @Operation(summary = "Update appointment status",
               description = "Update the status of an appointment (mark as completed, cancelled, no-show, etc.)")
    public ResponseEntity<ApiResponse<DoctorAppointmentDto>> updateAppointmentStatus(
            @Parameter(description = "Appointment ID", required = true)
            @PathVariable Long appointmentId,

            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Status update request", required = true)
            @Valid @RequestBody UpdateAppointmentStatusRequest request) {

        log.info("PUT /api/v1/doctor/appointments/{}/status - doctorId: {}, newStatus: {}",
                appointmentId, doctorId, request.getStatus());

        DoctorAppointmentDto appointment = doctorService.updateAppointmentStatus(
                appointmentId, doctorId, request);

        return ResponseEntity.ok(ApiResponse.<DoctorAppointmentDto>builder()
                .success(true)
                .message("Appointment status updated successfully")
                .data(appointment)
                .build());
    }

    @GetMapping("/patients/recent")
    @Operation(summary = "Get recent patients",
               description = "Get the most recently added or updated patients for the doctor")
    public ResponseEntity<ApiResponse<List<DoctorPatientDto>>> getRecentPatients(
            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Number of patients to return (default: 5)")
            @RequestParam(defaultValue = "5") Integer limit) {

        log.info("GET /api/v1/doctor/patients/recent - doctorId: {}, limit: {}", doctorId, limit);

        List<DoctorPatientDto> patients = doctorService.getRecentPatients(doctorId, limit);

        return ResponseEntity.ok(ApiResponse.<List<DoctorPatientDto>>builder()
                .success(true)
                .message("Recent patients retrieved successfully")
                .data(patients)
                .build());
    }

    @GetMapping("/appointments/{appointmentId}")
    @Operation(summary = "Get appointment details",
               description = "Get detailed information about a specific appointment including patient details, medical history, prescriptions, and previous appointments")
    public ResponseEntity<ApiResponse<AppointmentDetailsResponseDto>> getAppointmentDetails(
            @Parameter(description = "Appointment ID", required = true)
            @PathVariable Long appointmentId,

            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId) {

        log.info("GET /api/v1/doctor/appointments/{} - doctorId: {}", appointmentId, doctorId);

        AppointmentDetailsResponseDto appointmentDetails = doctorService.getAppointmentDetails(appointmentId, doctorId);

        return ResponseEntity.ok(ApiResponse.<AppointmentDetailsResponseDto>builder()
                .success(true)
                .message("Appointment details retrieved successfully")
                .data(appointmentDetails)
                .build());
    }

    @PutMapping("/appointments/{appointmentId}/complete")
    @Operation(summary = "Mark appointment as completed",
               description = "Mark a specific appointment as completed by the doctor")
    public ResponseEntity<ApiResponse<DoctorAppointmentDto>> completeAppointment(
            @Parameter(description = "Appointment ID", required = true)
            @PathVariable Long appointmentId,

            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId) {

        log.info("PUT /api/v1/doctor/appointments/{}/complete - doctorId: {}", appointmentId, doctorId);

        DoctorAppointmentDto completedAppointment = doctorService.completeAppointment(appointmentId, doctorId);

        return ResponseEntity.ok(ApiResponse.<DoctorAppointmentDto>builder()
                .success(true)
                .message("Appointment marked as completed successfully")
                .data(completedAppointment)
                .build());
    }

    @GetMapping("/profile")
    @Operation(summary = "Get doctor profile", description = "Retrieve doctor profile information including statistics")
    public ResponseEntity<ApiResponse<DoctorProfileResponseDto>> getDoctorProfile(
            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId) {

        log.info("GET /api/v1/doctor/profile - doctorId: {}", doctorId);

        DoctorProfileResponseDto profile = doctorService.getDoctorProfile(doctorId);

        return ResponseEntity.ok(ApiResponse.<DoctorProfileResponseDto>builder()
                .success(true)
                .message("Doctor profile retrieved successfully")
                .data(profile)
                .build());
    }
}
