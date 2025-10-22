package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.constants.ErrorMessages;
import com.mhms.medisynapse.constants.SuccessMessages;
import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.HospitalDto;
import com.mhms.medisynapse.dto.HospitalListResponse;
import com.mhms.medisynapse.dto.HospitalStatsDto;
import com.mhms.medisynapse.dto.PagedResponse;
import com.mhms.medisynapse.exception.InvalidDataException;
import com.mhms.medisynapse.service.HospitalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1/hospitals")
@RequiredArgsConstructor
@Slf4j
@Validated
public class HospitalController {

    // Constants for validation
    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String[] VALID_SORT_FIELDS = {"name", "type", "createdAt", "totalBeds", "availableBeds"};
    private final HospitalService hospitalService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<HospitalDto>> createHospital(
            @Valid @RequestBody HospitalDto hospitalDto) {

        log.info("Creating new hospital: {}", hospitalDto.getName());

        try {
            // Additional business validation
            validateHospitalBusinessRules(hospitalDto);

            HospitalDto createdHospital = hospitalService.createHospital(hospitalDto);

            log.info("Hospital created successfully with ID: {}", createdHospital.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(SuccessMessages.HOSPITAL_CREATED, createdHospital));

        } catch (Exception ex) {
            log.error("Failed to create hospital: {}", hospitalDto.getName(), ex);
            throw ex; // Let GlobalExceptionHandler handle it
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<HospitalDto>> updateHospital(
            @PathVariable @Min(value = 1, message = ErrorMessages.ID_MUST_BE_POSITIVE) Long id,
            @Valid @RequestBody HospitalDto hospitalDto) {

        log.info("Updating hospital: {}", id, hospitalDto.getName());

        try {
            // Validate ID is provided
            if (id == null || id <= 0) {
                throw new InvalidDataException(String.format(ErrorMessages.INVALID_ID_FORMAT, id));
            }

            // Additional business validation
            validateHospitalBusinessRules(hospitalDto);

            HospitalDto updatedHospital = hospitalService.updateHospital(id, hospitalDto);

            log.info("Hospital updated successfully with ID: {}", id);

            return ResponseEntity.ok(ApiResponse.success(SuccessMessages.HOSPITAL_UPDATED, updatedHospital));

        } catch (Exception ex) {
            log.error("Failed to update hospital with ID: {}", id, ex);
            throw ex; // Let GlobalExceptionHandler handle it
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHospital(
            @PathVariable @Min(value = 1, message = ErrorMessages.ID_MUST_BE_POSITIVE) Long id) {

        log.info("Soft deleting hospital", id);

        try {
            // Validate ID
            if (id == null || id <= 0) {
                throw new InvalidDataException(String.format(ErrorMessages.INVALID_ID_FORMAT, id));
            }

            hospitalService.deleteHospital(id);

            log.info("Hospital soft deleted successfully with ID: {}", id);

            return ResponseEntity.ok(ApiResponse.success(SuccessMessages.HOSPITAL_DELETED, null));

        } catch (Exception ex) {
            log.error("Failed to delete hospital with ID: {}", id, ex);
            throw ex; // Let GlobalExceptionHandler handle it
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HospitalListResponse> getAllHospitals(
            @RequestParam(value = "page", defaultValue = "0") @Min(value = 0, message = "Page number must be non-negative") int page,
            @RequestParam(value = "size", defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        try {
            validatePaginationAndSorting(size, sortBy, sortDir);

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

        } catch (Exception ex) {
            log.error("Failed to retrieve hospitals with pagination", ex);
            throw ex;
        }
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<HospitalDto>>> searchHospitals(
            @RequestParam(value = "name") @NotBlank(message = "Name parameter cannot be empty") String name) {

        log.info("hospitals/search - name: {}", name);

        try {
            // Validate and sanitize name parameter
            if (name == null || name.trim().isEmpty()) {
                throw new InvalidDataException(String.format(ErrorMessages.FIELD_CANNOT_BE_EMPTY, "Name parameter"));
            }

            String sanitizedName = name.trim();
            if (sanitizedName.length() > 100) {
                throw new InvalidDataException(String.format(ErrorMessages.FIELD_TOO_LONG, "Search name", 100));
            }

            List<HospitalDto> hospitals = hospitalService.searchHospitalsByName(sanitizedName);

            log.info("Successfully found {} hospitals matching name: {}", hospitals.size(), sanitizedName);

            return ResponseEntity.ok(ApiResponse.success(SuccessMessages.HOSPITAL_SEARCH_COMPLETED, hospitals));

        } catch (Exception ex) {
            log.error("Failed to search hospitals with name: {}", name, ex);
            throw ex; // Let GlobalExceptionHandler handle it
        }
    }

    @GetMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<HospitalStatsDto>> getHospitalStatistics() {
        log.info("Fetching hospital statistics");

        try {
            HospitalStatsDto stats = hospitalService.getHospitalStatistics();

            log.info("Successfully calculated hospital statistics - Total: {}, Active: {}",
                    stats.getTotalHospitals(), stats.getActiveHospitals());

            return ResponseEntity.ok(ApiResponse.success(SuccessMessages.HOSPITAL_STATISTICS_RETRIEVED, stats));

        } catch (Exception ex) {
            log.error("Failed to retrieve hospital statistics", ex);
            throw ex; // Let GlobalExceptionHandler handle it
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<HospitalDto>> getHospitalById(
            @PathVariable @Min(value = 1, message = ErrorMessages.ID_MUST_BE_POSITIVE) Long id) {


        try {
            // Validate ID
            if (id == null || id <= 0) {
                throw new InvalidDataException(String.format(ErrorMessages.INVALID_ID_FORMAT, id));
            }

            HospitalDto hospital = hospitalService.getActiveHospitalById(id);

            log.info("Successfully retrieved active hospital with ID: {}", id);

            return ResponseEntity.ok(ApiResponse.success(SuccessMessages.HOSPITAL_RETRIEVED, hospital));

        } catch (Exception ex) {
            log.error("Failed to retrieve hospital with ID: {}", id, ex);
            throw ex; // Let GlobalExceptionHandler handle it
        }
    }

    /**
     * Validates business rules for hospital data
     */
    private void validateHospitalBusinessRules(HospitalDto hospitalDto) {
        if (hospitalDto == null) {
            throw new InvalidDataException(ErrorMessages.FIELD_CANNOT_BE_NULL.replace("%s", "Hospital data"));
        }

        // Validate available beds doesn't exceed total beds
        if (hospitalDto.getAvailableBeds() != null && hospitalDto.getTotalBeds() != null) {
            if (hospitalDto.getAvailableBeds() > hospitalDto.getTotalBeds()) {
                throw new InvalidDataException(String.format(ErrorMessages.AVAILABLE_BEDS_EXCEED_TOTAL,
                        hospitalDto.getAvailableBeds(), hospitalDto.getTotalBeds()));
            }
        }

        // Validate hospital name uniqueness would be handled in service layer
        // Additional business validations can be added here
    }

    /**
     * Validates pagination and sorting parameters
     */
    private void validatePaginationAndSorting(int size, String sortBy, String sortDir) {
        // Validate page size
        if (size > MAX_PAGE_SIZE) {
            throw new InvalidDataException(String.format(ErrorMessages.INVALID_PAGE_SIZE, 1, MAX_PAGE_SIZE));
        }

        // Validate sort field
        boolean validSortField = false;
        for (String validField : VALID_SORT_FIELDS) {
            if (validField.equalsIgnoreCase(sortBy)) {
                validSortField = true;
                break;
            }
        }

        if (!validSortField) {
            throw new InvalidDataException(String.format(ErrorMessages.INVALID_SORT_FIELD,
                    sortBy, String.join(", ", VALID_SORT_FIELDS)));
        }

        // Validate sort direction
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new InvalidDataException(ErrorMessages.INVALID_SORT_DIRECTION);
        }
    }
}
