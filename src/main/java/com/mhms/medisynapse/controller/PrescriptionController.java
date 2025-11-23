package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.PrescriptionResponseDto;
import com.mhms.medisynapse.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Prescription Search", description = "General prescription retrieval endpoints")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping("/prescriptions")
    @Operation(summary = "Get prescriptions by doctor and hospital",
            description = "Retrieve prescriptions written by a specific doctor in a specific hospital. Supports pagination.")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<List<PrescriptionResponseDto>>> getPrescriptionsByDoctorAndHospital(
            @Parameter(description = "Doctor ID", required = true)
            @RequestParam Long doctorId,
            @Parameter(description = "Hospital ID", required = true)
            @RequestParam Long hospitalId,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/v1/prescriptions - doctorId: {}, hospitalId: {}, page: {}, size: {}", doctorId, hospitalId, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "prescriptionDate"));
        List<PrescriptionResponseDto> prescriptions = prescriptionService.getPrescriptionsByDoctorAndHospital(doctorId, hospitalId, pageable);

        String message = prescriptions.isEmpty() ? "No prescriptions found" : "Prescriptions retrieved successfully";

        return ResponseEntity.ok(ApiResponse.<List<PrescriptionResponseDto>>builder()
                .success(true)
                .message(message)
                .data(prescriptions)
                .build());
    }
}

