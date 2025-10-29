package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.CreatePatientRequestDto;
import com.mhms.medisynapse.dto.CreatePatientResponseDto;
import com.mhms.medisynapse.dto.PatientHistoryResponseDTO;
import com.mhms.medisynapse.dto.PatientListDto;
import com.mhms.medisynapse.dto.PatientPagedResponseDto;
import com.mhms.medisynapse.dto.UpdatePatientRequestDto;
import com.mhms.medisynapse.dto.UpdatePatientResponseDto;
import com.mhms.medisynapse.entity.Patient;
import com.mhms.medisynapse.entity.PatientHospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {
    PatientPagedResponseDto getPatientsByHospitalId(Long hospitalId,
                                                    Patient.PatientStatus status,
                                                    String search,
                                                    Pageable pageable);

    CreatePatientResponseDto createPatient(CreatePatientRequestDto request);

    UpdatePatientResponseDto updatePatient(Long patientId, UpdatePatientRequestDto request, Long hospitalId);

    void deletePatient(Long patientId, Long hospitalId);

    Page<Patient> getPatientsByHospitalAndStatus(Long hospitalId, PatientHospital.PatientHospitalStatus status, Pageable pageable);

    Page<Patient> getEligiblePatientsForHospital(Long hospitalId, Pageable pageable);

    Page<Patient> getAllPatients(Boolean onlyActive, Pageable pageable);

    void assignPatientToHospital(Long patientId, Long hospitalId);

    PatientListDto convertToPatientListDto(Patient patient);

    PatientHistoryResponseDTO getPatientFullHistory(Long patientId);
}
