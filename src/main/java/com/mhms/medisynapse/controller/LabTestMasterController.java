package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.entity.LabTestMaster;
import com.mhms.medisynapse.repository.LabTestMasterRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lab-tests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Lab Test Master", description = "Lab test catalog management")
public class LabTestMasterController {

    private final LabTestMasterRepository labTestMasterRepository;

    /**
     * GET /api/v1/lab-tests/available
     * Get all available lab tests
     */
    @GetMapping("/available")
    @Operation(summary = "Get available lab tests",
            description = "Retrieve list of all available lab tests from the catalog")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<ApiResponse<List<LabTestMaster>>> getAvailableLabTests() {
        log.info("GET /api/v1/lab-tests/available - Fetching all available lab tests");

        List<LabTestMaster> tests = labTestMasterRepository.findByIsActiveTrueOrderByTestNameAsc();

        return ResponseEntity.ok(ApiResponse.<List<LabTestMaster>>builder()
                .success(true)
                .message("Available lab tests retrieved successfully")
                .data(tests)
                .build());
    }

    /**
     * GET /api/v1/lab-tests/search
     * Search lab tests by name
     */
    @GetMapping("/search")
    @Operation(summary = "Search lab tests",
            description = "Search lab tests by name (case-insensitive)")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<ApiResponse<List<LabTestMaster>>> searchLabTests(
            @Parameter(description = "Search query", required = true)
            @RequestParam String query) {

        log.info("GET /api/v1/lab-tests/search - Query: {}", query);

        List<LabTestMaster> tests = labTestMasterRepository
                .findByTestNameContainingIgnoreCaseAndIsActiveTrue(query);

        return ResponseEntity.ok(ApiResponse.<List<LabTestMaster>>builder()
                .success(true)
                .message("Lab tests found successfully")
                .data(tests)
                .build());
    }

    /**
     * GET /api/v1/lab-tests/by-type/{testType}
     * Get lab tests by type
     */
    @GetMapping("/by-type/{testType}")
    @Operation(summary = "Get lab tests by type",
            description = "Retrieve lab tests filtered by test type (Blood, Urine, Imaging, etc.)")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<ApiResponse<List<LabTestMaster>>> getLabTestsByType(
            @Parameter(description = "Test type (Blood, Urine, Imaging, Biopsy, Other)", required = true)
            @PathVariable String testType) {

        log.info("GET /api/v1/lab-tests/by-type/{}", testType);

        List<LabTestMaster> tests = labTestMasterRepository.findByTestType(testType);

        return ResponseEntity.ok(ApiResponse.<List<LabTestMaster>>builder()
                .success(true)
                .message("Lab tests retrieved successfully")
                .data(tests)
                .build());
    }

    /**
     * GET /api/v1/lab-tests/by-category/{category}
     * Get lab tests by category
     */
    @GetMapping("/by-category/{category}")
    @Operation(summary = "Get lab tests by category",
            description = "Retrieve lab tests filtered by category")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN', 'HOSPITAL_ADMIN')")
    public ResponseEntity<ApiResponse<List<LabTestMaster>>> getLabTestsByCategory(
            @Parameter(description = "Test category", required = true)
            @PathVariable String category) {

        log.info("GET /api/v1/lab-tests/by-category/{}", category);

        List<LabTestMaster> tests = labTestMasterRepository.findByCategory(category);

        return ResponseEntity.ok(ApiResponse.<List<LabTestMaster>>builder()
                .success(true)
                .message("Lab tests retrieved successfully")
                .data(tests)
                .build());
    }
}

