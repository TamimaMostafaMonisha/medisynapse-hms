package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.constants.SuccessMessages;
import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.CreatePatientRequestDto;
import com.mhms.medisynapse.dto.CreatePatientResponseDto;
import com.mhms.medisynapse.dto.PatientPagedResponseDto;
import com.mhms.medisynapse.dto.UpdatePatientRequestDto;
import com.mhms.medisynapse.dto.UpdatePatientResponseDto;
import com.mhms.medisynapse.dto.PatientListDto;
import com.mhms.medisynapse.dto.PatientHistoryResponseDTO;
import com.mhms.medisynapse.entity.Patient;
import com.mhms.medisynapse.entity.PatientHospital;
import com.mhms.medisynapse.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hospital-admin")
@RequiredArgsConstructor
@Slf4j
public class PatientManagementController {

    private final PatientService patientService;

    @GetMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PatientPagedResponseDto>> getPatients(
            @RequestParam(required = false) Long hospitalId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Patient.PatientStatus status,
            @RequestParam(required = false) String search) {

        log.info("Hospital ID: {}, Page: {}, Size: {}, Status: {}, Search: {}",
                hospitalId, page, size, status, search);

        // Create pageable with sorting by created date (newest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDt"));

        PatientPagedResponseDto response = patientService.getPatientsByHospitalId(hospitalId, status, search, pageable);

        log.info("Successfully retrieved {} patients for hospital {}",
                response.getPatients().size(), hospitalId);

        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.PATIENT_LIST_RETRIEVED, response)
        );
    }

    @PostMapping(value = "/patients",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CreatePatientResponseDto>> createPatient(
            @Valid @RequestBody CreatePatientRequestDto request) {

        log.info("Creating new patient: {}", request.getName());

        CreatePatientResponseDto response = patientService.createPatient(request);

        log.info("Successfully created patient with ID: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessMessages.PATIENT_CREATED, response));
    }

    @PutMapping(value = "/patients/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UpdatePatientResponseDto>> updatePatient(
            @PathVariable Long patientId,
            @Valid @RequestBody UpdatePatientRequestDto request,
            @RequestParam Long hospitalId) {

        log.info("PUT /api/hospital-admin/patients/{} - Hospital ID: {}", patientId, hospitalId);

        try {
            UpdatePatientResponseDto response = patientService.updatePatient(patientId, request, hospitalId);

            log.info("Successfully updated patient with ID: {}", patientId);

            return ResponseEntity.ok(
                    ApiResponse.<UpdatePatientResponseDto>builder()
                            .success(true)
                            .message(SuccessMessages.PATIENT_UPDATED)
                            .data(response)
                            .build()
            );
        } catch (RuntimeException e) {
            log.error("Error updating patient with ID: {} - {}", patientId, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<UpdatePatientResponseDto>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @DeleteMapping(value = "/patients/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> deletePatient(
            @PathVariable Long patientId,
            @RequestParam Long hospitalId) {

        log.info("DELETE /api/hospital-admin/patients/{} - Hospital ID: {}", patientId, hospitalId);

        try {
            patientService.deletePatient(patientId, hospitalId);

            log.info("Successfully deleted patient with ID: {}", patientId);

            return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                            .success(true)
                            .message(SuccessMessages.PATIENT_DELETED)
                            .build()
            );
        } catch (RuntimeException e) {
            log.error("Error deleting patient with ID: {} - {}", patientId, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Void>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    // 1. List patients for a hospital by status
    @GetMapping("/patients")
    public ResponseEntity<ApiResponse<Page<PatientListDto>>> getPatientsByHospitalAndStatus(
            @RequestParam Long hospitalId,
            @RequestParam(required = false) PatientHospital.PatientHospitalStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDt"));
        Page<Patient> patients = patientService.getPatientsByHospitalAndStatus(hospitalId, status, pageable);
        Page<PatientListDto> dtoPage = patients.map(patientService::convertToPatientListDto);
        return ResponseEntity.ok(ApiResponse.success("Patient list retrieved", dtoPage));
    }

    // 2. List eligible patients for assignment to a hospital
    @GetMapping("/patients/eligible")
    public ResponseEntity<ApiResponse<Page<PatientListDto>>> getEligiblePatientsForHospital(
            @RequestParam Long hospitalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDt"));
        Page<Patient> patients = patientService.getEligiblePatientsForHospital(hospitalId, pageable);
        Page<PatientListDto> dtoPage = patients.map(patientService::convertToPatientListDto);
        return ResponseEntity.ok(ApiResponse.success("Eligible patients retrieved", dtoPage));
    }

    // 3. List all patients globally
    @GetMapping("/patients/all")
    public ResponseEntity<ApiResponse<Page<PatientListDto>>> getAllPatients(
            @RequestParam(defaultValue = "true") boolean onlyActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDt"));
        Page<Patient> patients = patientService.getAllPatients(onlyActive, pageable);
        Page<PatientListDto> dtoPage = patients.map(patientService::convertToPatientListDto);
        return ResponseEntity.ok(ApiResponse.success("All patients retrieved", dtoPage));
    }

    // 4. Assign/reactivate a patient in a hospital
    @PostMapping("/patients/{patientId}/assign-to-hospital")
    public ResponseEntity<ApiResponse<Void>> assignPatientToHospital(
            @PathVariable Long patientId,
            @RequestParam Long hospitalId) {
        patientService.assignPatientToHospital(patientId, hospitalId);
        return ResponseEntity.ok(ApiResponse.success("Patient assigned/reactivated in hospital", null));
    }

    // 5. Get a patient's complete medical history
    @GetMapping(value = "/patients/{patientId}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PatientHistoryResponseDTO>> getPatientFullHistory(@PathVariable Long patientId) {
        PatientHistoryResponseDTO history = patientService.getPatientFullHistory(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient history retrieved", history));
    }
}
