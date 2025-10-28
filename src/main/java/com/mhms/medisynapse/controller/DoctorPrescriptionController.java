package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.CreatePrescriptionRequest;
import com.mhms.medisynapse.dto.PrescriptionResponseDto;
import com.mhms.medisynapse.dto.UpdatePrescriptionRequest;
import com.mhms.medisynapse.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Doctor Prescription Management", description = "APIs for doctors to manage prescriptions")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorPrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping("/prescriptions")
    @Operation(summary = "Create prescription",
               description = "Create a new prescription for a patient during or after an appointment")
    public ResponseEntity<ApiResponse<PrescriptionResponseDto>> createPrescription(
            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Prescription details", required = true)
            @Valid @RequestBody CreatePrescriptionRequest request) {

        log.info("POST /api/v1/doctor/prescriptions - doctorId: {}, patientId: {}",
                doctorId, request.getPatientId());

        PrescriptionResponseDto prescription = prescriptionService.createPrescription(request, doctorId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PrescriptionResponseDto>builder()
                        .success(true)
                        .message("Prescription created successfully")
                        .data(prescription)
                        .build());
    }

    @GetMapping("/patients/{patientId}/prescriptions")
    @Operation(summary = "Get patient prescriptions",
               description = "Get all prescriptions for a specific patient prescribed by the requesting doctor")
    public ResponseEntity<ApiResponse<List<PrescriptionResponseDto>>> getPatientPrescriptions(
            @Parameter(description = "Patient ID", required = true)
            @PathVariable Long patientId,

            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Filter by status (Active, Completed, Cancelled)")
            @RequestParam(required = false) String status,

            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/v1/doctor/patients/{}/prescriptions - doctorId: {}, status: {}",
                patientId, doctorId, status);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "prescriptionDate"));
        List<PrescriptionResponseDto> prescriptions = prescriptionService
                .getPatientPrescriptions(patientId, doctorId, status, pageable);

        String message = prescriptions.isEmpty() ? "No prescriptions found" : "Prescriptions retrieved successfully";

        return ResponseEntity.ok(ApiResponse.<List<PrescriptionResponseDto>>builder()
                .success(true)
                .message(message)
                .data(prescriptions)
                .build());
    }

    @GetMapping("/appointments/{appointmentId}/prescriptions")
    @Operation(summary = "Get appointment prescriptions",
               description = "Get all prescriptions written during a specific appointment")
    public ResponseEntity<ApiResponse<List<PrescriptionResponseDto>>> getAppointmentPrescriptions(
            @Parameter(description = "Appointment ID", required = true)
            @PathVariable Long appointmentId,

            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId) {

        log.info("GET /api/v1/doctor/appointments/{}/prescriptions - doctorId: {}",
                appointmentId, doctorId);

        List<PrescriptionResponseDto> prescriptions = prescriptionService
                .getAppointmentPrescriptions(appointmentId, doctorId);

        return ResponseEntity.ok(ApiResponse.<List<PrescriptionResponseDto>>builder()
                .success(true)
                .message("Appointment prescriptions retrieved successfully")
                .data(prescriptions)
                .build());
    }

    @PutMapping("/prescriptions/{prescriptionId}")
    @Operation(summary = "Update prescription",
               description = "Update an existing prescription (e.g., change dosage, extend duration, cancel)")
    public ResponseEntity<ApiResponse<PrescriptionResponseDto>> updatePrescription(
            @Parameter(description = "Prescription ID", required = true)
            @PathVariable Long prescriptionId,

            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Updated prescription details", required = true)
            @Valid @RequestBody UpdatePrescriptionRequest request) {

        log.info("PUT /api/v1/doctor/prescriptions/{} - doctorId: {}", prescriptionId, doctorId);

        PrescriptionResponseDto prescription = prescriptionService
                .updatePrescription(prescriptionId, request, doctorId);

        return ResponseEntity.ok(ApiResponse.<PrescriptionResponseDto>builder()
                .success(true)
                .message("Prescription updated successfully")
                .data(prescription)
                .build());
    }
}

