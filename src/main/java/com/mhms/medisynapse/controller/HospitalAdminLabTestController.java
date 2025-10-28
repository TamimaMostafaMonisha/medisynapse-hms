package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.LabResultUpdateRequest;
import com.mhms.medisynapse.dto.LabTestOrderResponse;
import com.mhms.medisynapse.service.LabTestOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hospital-admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Hospital Admin - Lab Tests", description = "Lab test management for hospital administrators")
@PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN')")
public class HospitalAdminLabTestController {

    private final LabTestOrderService labTestOrderService;

    @Value("${file.upload-dir:uploads/lab-reports}")
    private String uploadDir;

    /**
     * GET /api/v1/hospital-admin/lab-tests/pending?hospitalId={hospitalId}
     * Get pending lab tests for a hospital
     */
    @GetMapping("/lab-tests/pending")
    @Operation(summary = "Get pending lab tests",
            description = "Get all lab tests that need processing (ORDERED, SAMPLE_COLLECTED, IN_PROGRESS)")
    public ResponseEntity<ApiResponse<List<LabTestOrderResponse>>> getPendingLabTests(
            @Parameter(description = "Hospital ID", required = true)
            @RequestParam Long hospitalId) {

        log.info("GET /api/v1/hospital-admin/lab-tests/pending - Hospital: {}", hospitalId);

        List<String> pendingStatuses = List.of("ORDERED", "SAMPLE_COLLECTED", "IN_PROGRESS");
        List<LabTestOrderResponse> responses = labTestOrderService.getLabOrdersByHospitalAndStatus(
                hospitalId, pendingStatuses);

        return ResponseEntity.ok(ApiResponse.<List<LabTestOrderResponse>>builder()
                .success(true)
                .message("Pending lab tests retrieved successfully")
                .data(responses)
                .build());
    }

    /**
     * GET /api/v1/hospital-admin/lab-tests/all?hospitalId={hospitalId}
     * Get all lab tests for a hospital
     */
    @GetMapping("/lab-tests/all")
    @Operation(summary = "Get all lab tests",
            description = "Get all lab tests for a hospital regardless of status")
    public ResponseEntity<ApiResponse<List<LabTestOrderResponse>>> getAllLabTests(
            @Parameter(description = "Hospital ID", required = true)
            @RequestParam Long hospitalId,

            @Parameter(description = "Filter by status (optional)")
            @RequestParam(required = false) List<String> statuses) {

        log.info("GET /api/v1/hospital-admin/lab-tests/all - Hospital: {}, Statuses: {}", hospitalId, statuses);

        // If no statuses provided, get all statuses
        if (statuses == null || statuses.isEmpty()) {
            statuses = List.of("ORDERED", "SAMPLE_COLLECTED", "IN_PROGRESS", "COMPLETED", "REVIEWED", "CANCELLED");
        }

        List<LabTestOrderResponse> responses = labTestOrderService.getLabOrdersByHospitalAndStatus(
                hospitalId, statuses);

        return ResponseEntity.ok(ApiResponse.<List<LabTestOrderResponse>>builder()
                .success(true)
                .message("Lab tests retrieved successfully")
                .data(responses)
                .build());
    }

    /**
     * GET /api/v1/hospital-admin/lab-tests/completed?hospitalId={hospitalId}
     * Get completed lab tests awaiting review
     */
    @GetMapping("/lab-tests/completed")
    @Operation(summary = "Get completed lab tests",
            description = "Get lab tests that are completed but not yet reviewed by doctor")
    public ResponseEntity<ApiResponse<List<LabTestOrderResponse>>> getCompletedLabTests(
            @Parameter(description = "Hospital ID", required = true)
            @RequestParam Long hospitalId) {

        log.info("GET /api/v1/hospital-admin/lab-tests/completed - Hospital: {}", hospitalId);

        List<String> completedStatuses = List.of("COMPLETED");
        List<LabTestOrderResponse> responses = labTestOrderService.getLabOrdersByHospitalAndStatus(
                hospitalId, completedStatuses);

        return ResponseEntity.ok(ApiResponse.<List<LabTestOrderResponse>>builder()
                .success(true)
                .message("Completed lab tests retrieved successfully")
                .data(responses)
                .build());
    }

    /**
     * PUT /api/v1/hospital-admin/lab-tests/{labTestId}/status
     * Update lab test status
     */
    @PutMapping("/lab-tests/{labTestId}/status")
    @Operation(summary = "Update lab test status",
            description = "Update the status of a lab test (collect sample, mark in progress, complete)")
    public ResponseEntity<ApiResponse<LabTestOrderResponse>> updateLabTestStatus(
            @Parameter(description = "Lab Test Order ID", required = true)
            @PathVariable Long labTestId,

            @Parameter(description = "Status update details", required = true)
            @Valid @RequestBody LabResultUpdateRequest request) {

        log.info("PUT /api/v1/hospital-admin/lab-tests/{}/status - New status: {}", labTestId, request.getStatus());

        LabTestOrderResponse response = labTestOrderService.updateLabTestStatus(labTestId, request);

        return ResponseEntity.ok(ApiResponse.<LabTestOrderResponse>builder()
                .success(true)
                .message("Lab test status updated successfully")
                .data(response)
                .build());
    }

    /**
     * POST /api/v1/hospital-admin/lab-tests/{labTestId}/upload
     * Upload lab test report file
     */
    @PostMapping(value = "/lab-tests/{labTestId}/upload", consumes = "multipart/form-data")
    @Operation(summary = "Upload lab test report",
            description = "Upload PDF report file for a lab test and mark as completed")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadLabTestReport(
            @Parameter(description = "Lab Test Order ID", required = true)
            @PathVariable Long labTestId,

            @Parameter(description = "Report file (PDF)", required = true)
            @RequestParam(value = "file", required = true) MultipartFile file) {

        log.info("POST /api/v1/hospital-admin/lab-tests/{}/upload - Filename: {}", labTestId,
                file != null ? file.getOriginalFilename() : "null");

        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.<FileUploadResponse>builder()
                    .success(false)
                    .message("File is empty")
                    .build());
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            return ResponseEntity.badRequest().body(ApiResponse.<FileUploadResponse>builder()
                    .success(false)
                    .message("Only PDF files are allowed")
                    .build());
        }

        try {
            // Create directory structure: uploads/lab-reports/YYYY-MM-DD/
            String dateFolder = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            Path uploadPath = Paths.get(uploadDir, dateFolder);
            Files.createDirectories(uploadPath);

            // Generate unique filename: originalName_UUID.pdf
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String baseFilename = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            String uniqueFilename = baseFilename + "_" + UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Generate file URL (relative path)
            String fileUrl = "/lab-reports/" + dateFolder + "/" + uniqueFilename;

            log.info("File uploaded successfully: {}", fileUrl);

            // Update lab test status to COMPLETED with report URL
            LabResultUpdateRequest updateRequest = LabResultUpdateRequest.builder()
                    .status("COMPLETED")
                    .reportFileUrl(fileUrl)
                    .build();

            LabTestOrderResponse updatedLabTest = labTestOrderService.updateLabTestStatus(labTestId, updateRequest);

            FileUploadResponse response = FileUploadResponse.builder()
                    .filename(uniqueFilename)
                    .originalFilename(originalFilename)
                    .fileUrl(fileUrl)
                    .fileSize(file.getSize())
                    .contentType(contentType)
                    .uploadedAt(java.time.LocalDateTime.now())
                    .labTestId(labTestId)
                    .labTestStatus(updatedLabTest.getStatus())
                    .build();

            return ResponseEntity.ok(ApiResponse.<FileUploadResponse>builder()
                    .success(true)
                    .message("Lab test report uploaded successfully")
                    .data(response)
                    .build());

        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.internalServerError().body(ApiResponse.<FileUploadResponse>builder()
                    .success(false)
                    .message("Failed to upload file: " + e.getMessage())
                    .build());
        }
    }

    /**
     * GET /api/v1/hospital-admin/lab-tests/{labTestId}
     * Get single lab test details
     */
    @GetMapping("/lab-tests/{labTestId}")
    @Operation(summary = "Get lab test details",
            description = "Get detailed information about a specific lab test order")
    public ResponseEntity<ApiResponse<LabTestOrderResponse>> getLabTestDetails(
            @Parameter(description = "Lab Test Order ID", required = true)
            @PathVariable Long labTestId) {

        log.info("GET /api/v1/hospital-admin/lab-tests/{}", labTestId);

        // This will be implemented by calling the service
        // For now, we can get it from the pending list and filter
        // In production, add a getLabTestById method to service

        return ResponseEntity.ok(ApiResponse.<LabTestOrderResponse>builder()
                .success(true)
                .message("Lab test details retrieved successfully")
                .data(null)  // TODO: Implement getById in service
                .build());
    }

    /**
     * GET /api/v1/hospital-admin/lab-tests/stats?hospitalId={hospitalId}
     * Get lab test statistics for dashboard
     */
    @GetMapping("/lab-tests/stats")
    @Operation(summary = "Get lab test statistics",
            description = "Get statistics about lab tests for hospital dashboard")
    public ResponseEntity<ApiResponse<LabTestStatsResponse>> getLabTestStats(
            @Parameter(description = "Hospital ID", required = true)
            @RequestParam Long hospitalId) {

        log.info("GET /api/v1/hospital-admin/lab-tests/stats - Hospital: {}", hospitalId);

        // Get counts for each status
        List<LabTestOrderResponse> ordered = labTestOrderService.getLabOrdersByHospitalAndStatus(
                hospitalId, List.of("ORDERED"));
        List<LabTestOrderResponse> sampleCollected = labTestOrderService.getLabOrdersByHospitalAndStatus(
                hospitalId, List.of("SAMPLE_COLLECTED"));
        List<LabTestOrderResponse> inProgress = labTestOrderService.getLabOrdersByHospitalAndStatus(
                hospitalId, List.of("IN_PROGRESS"));
        List<LabTestOrderResponse> completed = labTestOrderService.getLabOrdersByHospitalAndStatus(
                hospitalId, List.of("COMPLETED"));
        List<LabTestOrderResponse> reviewed = labTestOrderService.getLabOrdersByHospitalAndStatus(
                hospitalId, List.of("REVIEWED"));

        LabTestStatsResponse stats = LabTestStatsResponse.builder()
                .orderedCount(ordered.size())
                .sampleCollectedCount(sampleCollected.size())
                .inProgressCount(inProgress.size())
                .completedCount(completed.size())
                .reviewedCount(reviewed.size())
                .totalPending(ordered.size() + sampleCollected.size() + inProgress.size())
                .build();

        return ResponseEntity.ok(ApiResponse.<LabTestStatsResponse>builder()
                .success(true)
                .message("Lab test statistics retrieved successfully")
                .data(stats)
                .build());
    }

    // Inner DTO for stats
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class LabTestStatsResponse {
        private Integer orderedCount;
        private Integer sampleCollectedCount;
        private Integer inProgressCount;
        private Integer completedCount;
        private Integer reviewedCount;
        private Integer totalPending;
    }

    // Inner DTO for file upload response
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class FileUploadResponse {
        private String filename;
        private String originalFilename;
        private String fileUrl;
        private Long fileSize;
        private String contentType;
        private java.time.LocalDateTime uploadedAt;
        private Long labTestId;
        private String labTestStatus;
    }
}
