package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.CreatePatientRequestDto;
import com.mhms.medisynapse.dto.CreatePatientResponseDto;
import com.mhms.medisynapse.dto.PatientPagedResponseDto;
import com.mhms.medisynapse.dto.UpdatePatientRequestDto;
import com.mhms.medisynapse.dto.UpdatePatientResponseDto;
import com.mhms.medisynapse.entity.Patient;
import org.springframework.data.domain.Pageable;

public interface PatientService {
    PatientPagedResponseDto getPatientsByHospitalId(Long hospitalId,
                                                    Patient.PatientStatus status,
                                                    String search,
                                                    Pageable pageable);

    CreatePatientResponseDto createPatient(CreatePatientRequestDto request);

    UpdatePatientResponseDto updatePatient(Long patientId, UpdatePatientRequestDto request, Long hospitalId);

    void deletePatient(Long patientId, Long hospitalId);
}
