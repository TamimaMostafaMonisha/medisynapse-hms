package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.constants.ErrorMessages;
import com.mhms.medisynapse.constants.SuccessMessages;
import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.DepartmentTypeDto;
import com.mhms.medisynapse.service.DepartmentTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentTypeService departmentTypeService;

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<DepartmentTypeDto>>> getAvailableDepartments() {

        log.info("GET /api/v1/departments/available - Fetching available department types");

        try {
            List<DepartmentTypeDto> departments = departmentTypeService.getAllAvailableDepartmentTypes();

            log.info("Successfully retrieved {} available department types", departments.size());
            return ResponseEntity.ok(ApiResponse.success(SuccessMessages.DEPARTMENT_LIST_RETRIEVED, departments));

        } catch (Exception e) {
            log.error("Error retrieving available departments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorMessages.INTERNAL_SERVER_ERROR + ": " + e.getMessage()));
        }
    }
}
