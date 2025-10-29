package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.AppointmentDto;
import com.mhms.medisynapse.dto.AppointmentFilterDto;
import com.mhms.medisynapse.dto.AppointmentStatisticsDto;
import com.mhms.medisynapse.dto.CreateAppointmentRequestDto;
import com.mhms.medisynapse.dto.DoctorAvailabilityDto;
import com.mhms.medisynapse.dto.UpdateAppointmentRequestDto;
import com.mhms.medisynapse.entity.Appointment;
import com.mhms.medisynapse.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/hospital-admin")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "Hospital Admin Appointment Management APIs")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<Page<AppointmentDto>>> getAppointments(
            @Parameter(description = "Hospital ID") @RequestParam(required = false) Long hospitalId,
            @Parameter(description = "Doctor ID") @RequestParam(required = false) Long doctorId,
            @Parameter(description = "Patient ID") @RequestParam(required = false) Long patientId,
            @Parameter(description = "Department ID") @RequestParam(required = false) Long departmentId,
            @Parameter(description = "Appointment Status") @RequestParam(required = false) List<Appointment.AppointmentStatus> status,
            @Parameter(description = "Appointment Type") @RequestParam(required = false) Appointment.AppointmentType appointmentType,
            @Parameter(description = "Start Date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End Date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "startTime") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDirection) {

        AppointmentFilterDto filter = AppointmentFilterDto.builder()
                .hospitalId(hospitalId)
                .doctorId(doctorId)
                .patientId(patientId)
                .departmentId(departmentId)
                .status(status)
                .appointmentType(appointmentType)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        ApiResponse<Page<AppointmentDto>> response = appointmentService.getAppointments(filter);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/appointments")
    @Operation(summary = "Create new appointment",
            description = "Create a new appointment with validation and conflict checking")
    public ResponseEntity<ApiResponse<AppointmentDto>> createAppointment(
            @Valid @RequestBody CreateAppointmentRequestDto request,
            @Parameter(description = "Created by user ID") @RequestHeader(value = "X-User-ID", required = false, defaultValue = "1") Long createdBy) {

        ApiResponse<AppointmentDto> response = appointmentService.createAppointment(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/appointments/{appointmentId}")
    @Operation(summary = "Update/reschedule appointment",
            description = "Update appointment details or reschedule with availability validation")
    public ResponseEntity<ApiResponse<AppointmentDto>> updateAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long appointmentId,
            @Valid @RequestBody UpdateAppointmentRequestDto request,
            @Parameter(description = "Hospital ID for security") @RequestParam Long hospitalId,
            @Parameter(description = "Updated by user ID") @RequestHeader(value = "X-User-ID", required = false, defaultValue = "1") Long updatedBy) {

        // Combine appointmentDate and appointmentTime into startTime if provided and startTime is null
        if (request.getStartTime() == null && request.getAppointmentDate() != null && request.getAppointmentTime() != null) {
            request.setStartTime(request.getAppointmentDate().atTime(request.getAppointmentTime()));
        }

        ApiResponse<AppointmentDto> response = appointmentService.updateAppointment(appointmentId, request, hospitalId, updatedBy);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/appointments/{appointmentId}")
    @Operation(summary = "Cancel appointment",
            description = "Cancel appointment and free up the time slot")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long appointmentId,
            @Parameter(description = "Cancellation reason") @RequestParam(required = false) String cancellationReason,
            @Parameter(description = "Hospital ID for security") @RequestParam Long hospitalId,
            @Parameter(description = "Updated by user ID") @RequestHeader(value = "X-User-ID", required = false, defaultValue = "1") Long updatedBy) {

        ApiResponse<Void> response = appointmentService.cancelAppointment(appointmentId, cancellationReason, hospitalId, updatedBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctors/{doctorId}/availability")
    @Operation(summary = "Get doctor availability",
            description = "Get available time slots for a doctor on a specific date")
    public ResponseEntity<ApiResponse<DoctorAvailabilityDto>> getDoctorAvailability(
            @Parameter(description = "Doctor ID") @PathVariable Long doctorId,
            @Parameter(description = "Date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Slot interval in minutes") @RequestParam(required = false, defaultValue = "30") Integer slotIntervalMinutes) {

        ApiResponse<DoctorAvailabilityDto> response = appointmentService.getDoctorAvailability(doctorId, date, slotIntervalMinutes);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appointments/upcoming")
    @Operation(summary = "Get upcoming appointments",
            description = "Get upcoming appointments for dashboard display")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getUpcomingAppointments(
            @Parameter(description = "Hospital ID") @RequestParam Long hospitalId,
            @Parameter(description = "Number of days to look ahead") @RequestParam(required = false, defaultValue = "7") Integer days) {

        ApiResponse<List<AppointmentDto>> response = appointmentService.getUpcomingAppointments(hospitalId, days);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctors/{doctorId}/appointments/today")
    @Operation(summary = "Get today's appointments for doctor",
            description = "Get all appointments for a doctor today")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getTodayAppointmentsByDoctor(
            @Parameter(description = "Doctor ID") @PathVariable Long doctorId) {

        ApiResponse<List<AppointmentDto>> response = appointmentService.getTodayAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patients/{patientId}/appointments/history")
    @Operation(summary = "Get patient appointment history",
            description = "Get appointment history for a specific patient")
    public ResponseEntity<ApiResponse<Page<AppointmentDto>>> getPatientAppointmentHistory(
            @Parameter(description = "Patient ID") @PathVariable Long patientId,
            @Parameter(description = "Hospital ID") @RequestParam Long hospitalId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer size) {

        ApiResponse<Page<AppointmentDto>> response = appointmentService.getPatientAppointmentHistory(patientId, hospitalId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appointments/{appointmentId}")
    @Operation(summary = "Get appointment details",
            description = "Get detailed information about a specific appointment")
    public ResponseEntity<ApiResponse<AppointmentDto>> getAppointmentById(
            @Parameter(description = "Appointment ID") @PathVariable Long appointmentId,
            @Parameter(description = "Hospital ID for security") @RequestParam Long hospitalId) {

        AppointmentFilterDto filter = AppointmentFilterDto.builder()
                .hospitalId(hospitalId)
                .build();

        // Get single appointment using the filter service
        ApiResponse<Page<AppointmentDto>> response = appointmentService.getAppointments(filter);

        // Find the specific appointment
        AppointmentDto appointment = response.getData().getContent().stream()
                .filter(apt -> apt.getId().equals(appointmentId))
                .findFirst()
                .orElse(null);

        if (appointment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ApiResponse.<AppointmentDto>builder()
                .success(true)
                .message("Appointment details retrieved successfully")
                .data(appointment)
                .build());
    }

    @PatchMapping("/appointments/{appointmentId}/status")
    @Operation(summary = "Update appointment status",
            description = "Update only the status of an appointment (check-in, complete, etc.)")
    public ResponseEntity<ApiResponse<AppointmentDto>> updateAppointmentStatus(
            @Parameter(description = "Appointment ID") @PathVariable Long appointmentId,
            @Parameter(description = "New status") @RequestParam Appointment.AppointmentStatus status,
            @Parameter(description = "Hospital ID for security") @RequestParam Long hospitalId,
            @Parameter(description = "Updated by user ID") @RequestHeader(value = "X-User-ID", required = false, defaultValue = "1") Long updatedBy) {

        UpdateAppointmentRequestDto request = UpdateAppointmentRequestDto.builder()
                .status(status)
                .build();

        ApiResponse<AppointmentDto> response = appointmentService.updateAppointment(appointmentId, request, hospitalId, updatedBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appointments/statistics")
    public ResponseEntity<ApiResponse<AppointmentStatisticsDto>> getAppointmentStatistics(
            @Parameter(description = "Hospital ID") @RequestParam Long hospitalId,
            @Parameter(description = "Start Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Department ID") @RequestParam(required = false) Long departmentId,
            @Parameter(description = "Doctor ID") @RequestParam(required = false) Long doctorId) {

        // This would be implemented in the service layer
        AppointmentStatisticsDto statistics = AppointmentStatisticsDto.builder()
                .totalAppointments(0L)
                .scheduledAppointments(0L)
                .completedAppointments(0L)
                .cancelledAppointments(0L)
                .noShowAppointments(0L)
                .build();

        return ResponseEntity.ok(ApiResponse.<AppointmentStatisticsDto>builder()
                .success(true)
                .message("Appointment statistics retrieved successfully")
                .data(statistics)
                .build());
    }
}
