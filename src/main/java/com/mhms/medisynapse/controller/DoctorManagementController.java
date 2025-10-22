package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.constants.SuccessMessages;
import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.DoctorAvailabilityDto;
import com.mhms.medisynapse.dto.DoctorListResponseDto;
import com.mhms.medisynapse.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/hospital-admin")
@RequiredArgsConstructor
@Slf4j
public class DoctorManagementController {

    private final UserService userService;

    @GetMapping(value = "/doctors", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DoctorListResponseDto>> getDoctors(
            @RequestParam Long hospitalId) {

        log.info("GET /api/v1/hospital-admin/doctors - Fetching doctors for hospital ID: {}", hospitalId);

        DoctorListResponseDto doctors = userService.getDoctorsByHospital(hospitalId);

        log.info("Retrieved {} doctors for hospital ID: {}", doctors.getDoctors().size(), hospitalId);

        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.DOCTORS_RETRIEVED, doctors)
        );
    }

    @GetMapping(value = "/doctors/{doctorId}/availability", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<DoctorAvailabilityDto>> getDoctorAvailability(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("GET /api/v1/hospital-admin/doctors/{}/availability - Fetching availability for date: {}",
                doctorId, date);

        DoctorAvailabilityDto availability = userService.getDoctorAvailability(doctorId, date);

        log.info("Doctor availability retrieved successfully for doctor ID: {} on date: {}", doctorId, date);

        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.DOCTOR_AVAILABILITY_RETRIEVED, availability)
        );
    }
}
