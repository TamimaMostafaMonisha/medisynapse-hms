package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.HospitalDto;
import com.mhms.medisynapse.dto.HospitalListResponse;
import com.mhms.medisynapse.dto.HospitalStatsDto;
import com.mhms.medisynapse.dto.PagedResponse;
import com.mhms.medisynapse.service.HospitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hospitals")
@RequiredArgsConstructor
@Slf4j
public class HospitalController {

    private final HospitalService hospitalService;

    @PostMapping
    public ResponseEntity<ApiResponse<HospitalDto>> createHospital(@Valid @RequestBody HospitalDto hospitalDto) {
        log.info("POST /api/v1/hospitals - Creating new hospital: {}", hospitalDto.getName());

        HospitalDto createdHospital = hospitalService.createHospital(hospitalDto);
        log.info("Hospital created successfully with ID: {}", createdHospital.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hospital created successfully", createdHospital));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HospitalDto>> updateHospital(
            @PathVariable Long id,
            @Valid @RequestBody HospitalDto hospitalDto) {

        log.info("PUT /api/v1/hospitals/{} - Updating hospital: {}", id, hospitalDto.getName());

        HospitalDto updatedHospital = hospitalService.updateHospital(id, hospitalDto);
        log.info("Hospital updated successfully with ID: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Hospital updated successfully", updatedHospital));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHospital(@PathVariable Long id) {
        log.info("DELETE /api/v1/hospitals/{} - Soft deleting hospital", id);

        hospitalService.deleteHospital(id);
        log.info("Hospital soft deleted successfully with ID: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Hospital deleted successfully", null));
    }

    @GetMapping
    public ResponseEntity<HospitalListResponse> getAllHospitals(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        log.info("GET /api/v1/hospitals - page: {}, size: {}, sortBy: {}, sortDir: {}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<HospitalDto> pagedResponse = hospitalService.getAllActiveHospitals(pageable);

        // Build pagination info
        HospitalListResponse.PaginationInfo paginationInfo = HospitalListResponse.PaginationInfo.builder()
                .currentPage(pagedResponse.getPageNumber())
                .totalPages(pagedResponse.getTotalPages())
                .totalElements(pagedResponse.getTotalElements())
                .pageSize(pagedResponse.getPageSize())
                .hasNext(pagedResponse.isHasNext())
                .hasPrevious(pagedResponse.isHasPrevious())
                .build();

        HospitalListResponse response = HospitalListResponse.success(pagedResponse.getContent(), paginationInfo);

        log.info("Successfully retrieved {} active hospitals, page {}/{}",
                pagedResponse.getContent().size(),
                pagedResponse.getPageNumber() + 1,
                pagedResponse.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<HospitalDto>>> searchHospitals(@RequestParam(value = "name") String name) {
        log.info("GET /api/v1/hospitals/search - name: {}", name);

        // Validate name parameter
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name parameter is required and cannot be empty");
        }

        List<HospitalDto> hospitals = hospitalService.searchHospitalsByName(name.trim());
        log.info("Successfully found {} hospitals matching name: {}", hospitals.size(), name);

        return ResponseEntity.ok(ApiResponse.success("Hospitals retrieved successfully", hospitals));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<HospitalStatsDto>> getHospitalStatistics() {
        log.info("GET /api/v1/hospitals/stats - Fetching hospital statistics");

        HospitalStatsDto stats = hospitalService.getHospitalStatistics();
        log.info("Successfully calculated hospital statistics - Total: {}, Active: {}",
                stats.getTotalHospitals(), stats.getActiveHospitals());

        return ResponseEntity.ok(ApiResponse.success("Hospital statistics retrieved successfully", stats));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HospitalDto>> getHospitalById(@PathVariable Long id) {
        log.info("GET /api/v1/hospitals/{}", id);

        HospitalDto hospital = hospitalService.getActiveHospitalById(id);
        log.info("Successfully retrieved active hospital with ID: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Hospital retrieved successfully", hospital));
    }
}
