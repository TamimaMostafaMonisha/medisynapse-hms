package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.constants.SuccessMessages;
import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.AvailableHospitalDto;
import com.mhms.medisynapse.dto.CreateHospitalAdminDto;
import com.mhms.medisynapse.dto.HospitalAdminPagedResponseDto;
import com.mhms.medisynapse.dto.HospitalAdminResponseDto;
import com.mhms.medisynapse.dto.PasswordResetResponseDto;
import com.mhms.medisynapse.dto.ResetPasswordDto;
import com.mhms.medisynapse.dto.UpdateHospitalAdminDto;
import com.mhms.medisynapse.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/hospital-admins")
@RequiredArgsConstructor
@Slf4j
public class HospitalAdminController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<HospitalAdminResponseDto>> createHospitalAdmin(
            @Valid @RequestBody CreateHospitalAdminDto createHospitalAdminDto) {

        log.info("POST /api/v1/hospital-admins - Creating hospital admin: {}", createHospitalAdminDto.getName());

        HospitalAdminResponseDto createdAdmin = userService.createHospitalAdmin(createHospitalAdminDto);

        log.info("Hospital admin created successfully with ID: {}", createdAdmin.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessMessages.HOSPITAL_ADMIN_CREATED, createdAdmin));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<HospitalAdminResponseDto>> updateHospitalAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHospitalAdminDto updateHospitalAdminDto) {

        log.info("PUT /api/v1/hospital-admins/{} - Updating hospital admin", id);

        HospitalAdminResponseDto updatedAdmin = userService.updateHospitalAdmin(id, updateHospitalAdminDto);

        log.info("Hospital admin updated successfully with ID: {}", updatedAdmin.getId());

        return ResponseEntity.ok()
                .body(ApiResponse.success(SuccessMessages.HOSPITAL_ADMIN_UPDATED, updatedAdmin));
    }

    @PutMapping(value = "/{id}/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PasswordResetResponseDto>> resetHospitalAdminPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordDto resetPasswordDto) {

        log.info("PUT /api/v1/hospital-admins/{}/reset-password - Resetting password for hospital admin", id);

        PasswordResetResponseDto resetResponse = userService.resetHospitalAdminPassword(id, resetPasswordDto);

        log.info("Password reset successfully for hospital admin with ID: {}", resetResponse.getId());

        return ResponseEntity.ok()
                .body(ApiResponse.success(SuccessMessages.PASSWORD_RESET, resetResponse));
    }

    @GetMapping(value = "/available-hospitals", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<AvailableHospitalDto>>> getAvailableHospitals() {

        log.info("GET /api/v1/hospital-admins/available-hospitals - Fetching available hospitals");

        List<AvailableHospitalDto> availableHospitals = userService.getAvailableHospitals();

        log.info("Found {} available hospitals", availableHospitals.size());

        return ResponseEntity.ok()
                .body(ApiResponse.success(SuccessMessages.HOSPITAL_LIST_RETRIEVED, availableHospitals));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<HospitalAdminPagedResponseDto>> getHospitalAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Long hospitalId) {

        log.info("GET /api/v1/hospital-admins - page: {}, size: {}, sortBy: {}, sortDir: {}, hospitalId: {}",
                page, size, sortBy, sortDir, hospitalId);

        HospitalAdminPagedResponseDto pagedResponse = userService.getHospitalAdmins(page, size, sortBy, sortDir, hospitalId);

        log.info("Retrieved {} hospital admins (total: {})",
                pagedResponse.getData().size(), pagedResponse.getPagination().getTotalElements());

        return ResponseEntity.ok()
                .body(ApiResponse.success(SuccessMessages.HOSPITAL_ADMIN_LIST_RETRIEVED, pagedResponse));
    }
}
