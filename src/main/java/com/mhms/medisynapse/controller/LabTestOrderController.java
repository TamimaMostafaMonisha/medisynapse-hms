package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.LabResultReviewRequest;
import com.mhms.medisynapse.dto.LabResultUpdateRequest;
import com.mhms.medisynapse.dto.LabTestOrderRequest;
import com.mhms.medisynapse.dto.LabTestOrderResponse;
import com.mhms.medisynapse.service.LabTestOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Lab Test Orders", description = "Lab test ordering and management APIs")
public class LabTestOrderController {

    private final LabTestOrderService labTestOrderService;

    /**
     * POST /api/v1/appointments/{appointmentId}/lab-orders
     * Create lab test orders for an appointment
     */
    @PostMapping("/appointments/{appointmentId}/lab-orders")
    @Operation(summary = "Order lab tests", description = "Create lab test orders for a specific appointment")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<LabTestOrderResponse>>> orderLabTests(
            @Parameter(description = "Appointment ID", required = true)
            @PathVariable Long appointmentId,

            @Parameter(description = "List of lab test orders", required = true)
            @Valid @RequestBody List<LabTestOrderRequest> requests) {

        log.info("POST /api/v1/appointments/{}/lab-orders - Creating {} lab test orders",
                appointmentId, requests.size());

        List<LabTestOrderResponse> responses = labTestOrderService.createLabTestOrders(appointmentId, requests);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<List<LabTestOrderResponse>>builder()
                        .success(true)
                        .message("Lab test orders created successfully")
                        .data(responses)
                        .build());
    }

    /**
     * GET /api/v1/appointments/{appointmentId}/lab-orders
     * Get all lab orders for an appointment
     */
    @GetMapping("/appointments/{appointmentId}/lab-orders")
    @Operation(summary = "Get lab orders for appointment",
            description = "Retrieve all lab test orders for a specific appointment")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<ApiResponse<List<LabTestOrderResponse>>> getLabOrdersForAppointment(
            @Parameter(description = "Appointment ID", required = true)
            @PathVariable Long appointmentId) {

        log.info("GET /api/v1/appointments/{}/lab-orders", appointmentId);

        List<LabTestOrderResponse> responses = labTestOrderService.getLabOrdersForAppointment(appointmentId);

        return ResponseEntity.ok(ApiResponse.<List<LabTestOrderResponse>>builder()
                .success(true)
                .message("Lab orders retrieved successfully")
                .data(responses)
                .build());
    }

    /**
     * GET /api/v1/doctors/{doctorId}/lab-results/pending-review
     * Get lab results pending review for a doctor
     */
    @GetMapping("/doctors/{doctorId}/lab-results/pending-review")
    @Operation(summary = "Get pending lab results",
            description = "Get lab test results awaiting doctor's review")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<LabTestOrderResponse>>> getPendingLabResults(
            @Parameter(description = "Doctor ID", required = true)
            @PathVariable Long doctorId) {

        log.info("GET /api/v1/doctors/{}/lab-results/pending-review", doctorId);

        List<LabTestOrderResponse> responses = labTestOrderService.getPendingLabResultsForDoctor(doctorId);

        return ResponseEntity.ok(ApiResponse.<List<LabTestOrderResponse>>builder()
                .success(true)
                .message("Pending lab results retrieved successfully")
                .data(responses)
                .build());
    }

    /**
     * PUT /api/v1/lab-orders/{labOrderId}/status
     * Update lab test status (for hospital admin)
     */
    @PutMapping("/lab-orders/{labOrderId}/status")
    @Operation(summary = "Update lab test status",
            description = "Update the status of a lab test order (Hospital Admin)")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<ApiResponse<LabTestOrderResponse>> updateLabTestStatus(
            @Parameter(description = "Lab Order ID", required = true)
            @PathVariable Long labOrderId,

            @Parameter(description = "Lab test status update", required = true)
            @Valid @RequestBody LabResultUpdateRequest request) {

        log.info("PUT /api/v1/lab-orders/{}/status - Updating to status: {}", labOrderId, request.getStatus());

        LabTestOrderResponse response = labTestOrderService.updateLabTestStatus(labOrderId, request);

        return ResponseEntity.ok(ApiResponse.<LabTestOrderResponse>builder()
                .success(true)
                .message("Lab test status updated successfully")
                .data(response)
                .build());
    }

    /**
     * PUT /api/v1/lab-results/{labResultId}/review
     * Mark lab result as reviewed by doctor
     */
    @PutMapping("/lab-results/{labResultId}/review")
    @Operation(summary = "Review lab result",
            description = "Mark a lab result as reviewed by doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<LabTestOrderResponse>> reviewLabResult(
            @Parameter(description = "Lab Result ID", required = true)
            @PathVariable Long labResultId,

            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Review notes (optional)")
            @Valid @RequestBody(required = false) LabResultReviewRequest request) {

        log.info("PUT /api/v1/lab-results/{}/review - Doctor: {}", labResultId, doctorId);

        if (request == null) {
            request = new LabResultReviewRequest();
        }

        LabTestOrderResponse response = labTestOrderService.markLabResultAsReviewed(
                labResultId, doctorId, request);

        return ResponseEntity.ok(ApiResponse.<LabTestOrderResponse>builder()
                .success(true)
                .message("Lab result marked as reviewed successfully")
                .data(response)
                .build());
    }

    /**
     * GET /api/v1/hospitals/{hospitalId}/lab-orders
     * Get lab orders for a hospital by status (Hospital Admin)
     */
    @GetMapping("/hospitals/{hospitalId}/lab-orders")
    @Operation(summary = "Get hospital lab orders",
            description = "Get lab orders for a hospital filtered by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<ApiResponse<List<LabTestOrderResponse>>> getHospitalLabOrders(
            @Parameter(description = "Hospital ID", required = true)
            @PathVariable Long hospitalId,

            @Parameter(description = "List of statuses to filter (e.g., ORDERED, COMPLETED)")
            @RequestParam(required = false) List<String> statuses) {

        log.info("GET /api/v1/hospitals/{}/lab-orders - Statuses: {}", hospitalId, statuses);

        if (statuses == null || statuses.isEmpty()) {
            statuses = List.of("ORDERED", "SAMPLE_COLLECTED", "IN_PROGRESS");
        }

        List<LabTestOrderResponse> responses = labTestOrderService.getLabOrdersByHospitalAndStatus(
                hospitalId, statuses);

        return ResponseEntity.ok(ApiResponse.<List<LabTestOrderResponse>>builder()
                .success(true)
                .message("Lab orders retrieved successfully")
                .data(responses)
                .build());
    }
}

