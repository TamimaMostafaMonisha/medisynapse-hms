package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.CreatePrescriptionRequest;
import com.mhms.medisynapse.dto.PrescriptionResponseDto;
import com.mhms.medisynapse.dto.UpdatePrescriptionRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PrescriptionService {

    PrescriptionResponseDto createPrescription(CreatePrescriptionRequest request, Long doctorId);

    List<PrescriptionResponseDto> getPatientPrescriptions(Long patientId, Long doctorId, String status, Pageable pageable);

    List<PrescriptionResponseDto> getAppointmentPrescriptions(Long appointmentId, Long doctorId);

    PrescriptionResponseDto updatePrescription(Long prescriptionId, UpdatePrescriptionRequest request, Long doctorId);

    List<PrescriptionResponseDto> getPatientPrescriptionsByHospital(Long patientId, Long hospitalId);
}

