package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.ReceptionistResponseDto;
import com.mhms.medisynapse.security.CustomUserDetails;
import com.mhms.medisynapse.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/receptionist")
@RequiredArgsConstructor
@Slf4j
public class ReceptionistDashboardController {

    private final DashboardService dashboardService;

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ReceptionistResponseDto>> getAuthenticatedReceptionistProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized"));
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (!"RECEPTIONIST".equals(userDetails.getRole().name())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Forbidden: Not a receptionist"));
        }
        ReceptionistResponseDto profile = dashboardService.getReceptionistProfile(userDetails.getId());
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Profile not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", profile));
    }
}
