package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.ComprehensivePrescriptionResponse;
import com.mhms.medisynapse.dto.CreatePrescriptionRequest;
import com.mhms.medisynapse.dto.LabTestOrderResponse;
import com.mhms.medisynapse.dto.PrescriptionHistoryItem;
import com.mhms.medisynapse.dto.PrescriptionResponseDto;
import com.mhms.medisynapse.dto.PrescriptionWithTestsRequest;
import com.mhms.medisynapse.dto.UpdatePrescriptionRequest;
import com.mhms.medisynapse.entity.Appointment;
import com.mhms.medisynapse.entity.Hospital;
import com.mhms.medisynapse.entity.LabTestOrder;
import com.mhms.medisynapse.entity.Patient;
import com.mhms.medisynapse.entity.Prescription;
import com.mhms.medisynapse.entity.User;
import com.mhms.medisynapse.exception.ResourceNotFoundException;
import com.mhms.medisynapse.repository.AppointmentRepository;
import com.mhms.medisynapse.repository.HospitalRepository;
import com.mhms.medisynapse.repository.LabTestOrderRepository;
import com.mhms.medisynapse.repository.PatientRepository;
import com.mhms.medisynapse.repository.PrescriptionRepository;
import com.mhms.medisynapse.repository.UserRepository;
import com.mhms.medisynapse.service.LabTestOrderService;
import com.mhms.medisynapse.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final HospitalRepository hospitalRepository;
    private final LabTestOrderService labTestOrderService;
    private final LabTestOrderRepository labTestOrderRepository;

    @Override
    public PrescriptionResponseDto createPrescription(CreatePrescriptionRequest request, Long doctorId) {
        log.info("Creating prescription for patient {} by doctor {}", request.getPatientId(), doctorId);

        // Validate doctor
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));

        if (doctor.getRole() != User.UserRole.DOCTOR) {
            throw new IllegalArgumentException("User is not a doctor");
        }

        // Validate patient
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.getPatientId()));

        // Validate appointment
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + request.getAppointmentId()));

        // Verify appointment belongs to doctor
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("Appointment does not belong to this doctor");
        }

        // Verify appointment is for the patient
        if (!appointment.getPatient().getId().equals(request.getPatientId())) {
            throw new IllegalArgumentException("Appointment is not for this patient");
        }

        // Get hospital
        Hospital hospital = doctor.getHospital();
        if (hospital == null) {
            throw new IllegalStateException("Doctor is not assigned to a hospital");
        }

        // Create prescription
        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setHospital(hospital);
        prescription.setAppointment(appointment);
        prescription.setMedicationName(request.getMedicationName());
        prescription.setDosage(request.getDosage());
        prescription.setFrequency(request.getFrequency());
        prescription.setDuration(request.getDuration());
        prescription.setInstructions(request.getInstructions());
        prescription.setNotes(request.getNotes());
        prescription.setPrescriptionDate(LocalDate.now());
        prescription.setStatus(Prescription.PrescriptionStatus.ACTIVE);
        prescription.setCreatedBy(doctorId);
        prescription.setIsActive(true);

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        log.info("Prescription created successfully with ID: {}", savedPrescription.getId());

        return mapToResponseDto(savedPrescription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getPatientPrescriptions(Long patientId, Long doctorId, String status, Pageable pageable) {
        log.info("Fetching prescriptions for patient {} by doctor {}", patientId, doctorId);

        // Validate doctor
        validateDoctor(doctorId);

        // Validate patient
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with ID: " + patientId);
        }

        Prescription.PrescriptionStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = Prescription.PrescriptionStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status value: {}", status);
            }
        }

        List<Prescription> prescriptions = prescriptionRepository
                .findByPatientAndDoctorWithFilter(patientId, doctorId, statusEnum, pageable)
                .getContent();

        return prescriptions.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getAppointmentPrescriptions(Long appointmentId, Long doctorId) {
        log.info("Fetching prescriptions for appointment {} by doctor {}", appointmentId, doctorId);

        // Validate doctor
        validateDoctor(doctorId);

        // Validate appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        // Verify appointment belongs to doctor
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("Appointment does not belong to this doctor");
        }

        List<Prescription> prescriptions = prescriptionRepository.findByAppointmentAndDoctor(appointmentId, doctorId);

        return prescriptions.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PrescriptionResponseDto updatePrescription(Long prescriptionId, UpdatePrescriptionRequest request, Long doctorId) {
        log.info("Updating prescription {} by doctor {}", prescriptionId, doctorId);

        // Validate doctor
        validateDoctor(doctorId);

        // Find prescription
        Prescription prescription = prescriptionRepository.findByIdAndDoctor(prescriptionId, doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prescription not found with ID: " + prescriptionId + " for doctor: " + doctorId));

        // Update fields if provided
        if (request.getMedicationName() != null) {
            prescription.setMedicationName(request.getMedicationName());
        }
        if (request.getDosage() != null) {
            prescription.setDosage(request.getDosage());
        }
        if (request.getFrequency() != null) {
            prescription.setFrequency(request.getFrequency());
        }
        if (request.getDuration() != null) {
            prescription.setDuration(request.getDuration());
        }
        if (request.getInstructions() != null) {
            prescription.setInstructions(request.getInstructions());
        }
        if (request.getNotes() != null) {
            prescription.setNotes(request.getNotes());
        }
        if (request.getStatus() != null) {
            try {
                prescription.setStatus(Prescription.PrescriptionStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + request.getStatus());
            }
        }

        prescription.setUpdatedBy(doctorId);

        Prescription updatedPrescription = prescriptionRepository.save(prescription);

        log.info("Prescription {} updated successfully", prescriptionId);

        return mapToResponseDto(updatedPrescription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getPatientPrescriptionsByHospital(Long patientId, Long hospitalId) {
        log.info("Fetching prescriptions for patient {} in hospital {}", patientId, hospitalId);

        // Validate patient
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with ID: " + patientId);
        }

        // Validate hospital
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException("Hospital not found with ID: " + hospitalId);
        }

        List<Prescription> prescriptions = prescriptionRepository.findByPatientAndHospital(patientId, hospitalId);

        return prescriptions.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ComprehensivePrescriptionResponse createComprehensivePrescription(Long appointmentId, PrescriptionWithTestsRequest request) {
        log.info("Creating comprehensive prescription for appointment: {}", appointmentId);
        log.info("Prescription type: {}, Medications: {}, Lab tests: {}",
                request.getPrescriptionType(),
                request.getMedications() != null ? request.getMedications().size() : 0,
                request.getLabTestOrders() != null ? request.getLabTestOrders().size() : 0);

        // Validation: At least one of medications or labTestOrders must be present
        boolean hasMeds = request.getMedications() != null && !request.getMedications().isEmpty();
        boolean hasLabs = request.getLabTestOrders() != null && !request.getLabTestOrders().isEmpty();
        if (!hasMeds && !hasLabs) {
            throw new IllegalArgumentException("At least one of medications or labTestOrders must be provided.");
        }

        // Validate appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        // Validate doctor
        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        // Validate patient
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        List<Long> prescriptionIds = new ArrayList<>();
        List<Long> labOrderIds = new ArrayList<>();

        // If medications are present, create a prescription for each medication
        if (hasMeds) {
            for (PrescriptionWithTestsRequest.MedicationDto med : request.getMedications()) {
                Prescription prescription = new Prescription();
                prescription.setPatient(patient);
                prescription.setDoctor(doctor);
                prescription.setHospital(appointment.getHospital());
                prescription.setAppointment(appointment);
                prescription.setMedicationName(med.getMedicationName());
                prescription.setDosage(med.getDosage());
                prescription.setFrequency(med.getFrequency());
                prescription.setDuration(med.getDuration());
                prescription.setInstructions(request.getInstructions());
                prescription.setNotes(request.getNotes());
                prescription.setPrescriptionDate(LocalDate.now());
                prescription.setPrescriptionType(Prescription.PrescriptionType.valueOf(request.getPrescriptionType()));
                prescription.setClinicalDiagnosis(request.getClinicalDiagnosis());
                prescription.setFollowUpRequired(request.getFollowUpRequired());
                prescription.setFollowUpDate(request.getFollowUpDate());
                prescription.setStatus(Prescription.PrescriptionStatus.ACTIVE);
                prescription.setCreatedBy(request.getDoctorId());
                prescription.setIsActive(true);

                Prescription saved = prescriptionRepository.save(prescription);
                prescriptionIds.add(saved.getId());
            }
            log.info("Created {} prescriptions", prescriptionIds.size());
        }

        // If no medications but labTestOrders exist, create a single prescription for the lab orders
        if (!hasMeds && hasLabs) {
            Prescription prescription = new Prescription();
            prescription.setPatient(patient);
            prescription.setDoctor(doctor);
            prescription.setHospital(appointment.getHospital());
            prescription.setAppointment(appointment);
            // Set medication_name to empty string to satisfy NOT NULL constraint
            prescription.setMedicationName("");
            prescription.setInstructions(request.getInstructions());
            prescription.setNotes(request.getNotes());
            prescription.setPrescriptionDate(LocalDate.now());
            prescription.setPrescriptionType(Prescription.PrescriptionType.valueOf(request.getPrescriptionType()));
            prescription.setClinicalDiagnosis(request.getClinicalDiagnosis());
            prescription.setFollowUpRequired(request.getFollowUpRequired());
            prescription.setFollowUpDate(request.getFollowUpDate());
            prescription.setStatus(Prescription.PrescriptionStatus.ACTIVE);
            prescription.setCreatedBy(request.getDoctorId());
            prescription.setIsActive(true);

            Prescription saved = prescriptionRepository.save(prescription);
            prescriptionIds.add(saved.getId());
            log.info("Created 1 prescription for lab orders only");
        }

        // Create lab test orders
        if (hasLabs) {
            List<LabTestOrderResponse> labOrders = labTestOrderService.createLabTestOrders(
                    appointmentId,
                    request.getLabTestOrders()
            );
            labOrderIds = labOrders.stream()
                    .map(LabTestOrderResponse::getId)
                    .collect(Collectors.toList());
            log.info("Created {} lab test orders", labOrderIds.size());

            // Link lab orders to first prescription if any prescription exists
            if (!prescriptionIds.isEmpty()) {
                Long firstPrescriptionId = prescriptionIds.get(0);
                Prescription firstPrescription = prescriptionRepository.findById(firstPrescriptionId)
                        .orElseThrow();

                labOrders.forEach(order -> {
                    LabTestOrder labOrder = labTestOrderRepository.findById(order.getId())
                            .orElseThrow();
                    labOrder.setPrescription(firstPrescription);
                    labTestOrderRepository.save(labOrder);
                });
            }
        }

        return ComprehensivePrescriptionResponse.builder()
                .prescriptionId(!prescriptionIds.isEmpty() ? prescriptionIds.get(0) : null)
                .prescriptionIds(prescriptionIds)
                .labOrderIds(labOrderIds)
                .prescriptionType(request.getPrescriptionType())
                .medicationCount(prescriptionIds.size())
                .labTestCount(labOrderIds.size())
                .message("Comprehensive prescription created successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionHistoryItem> getPrescriptionHistory(Long appointmentId) {
        log.info("Fetching prescription history for appointment: {}", appointmentId);

        List<Prescription> prescriptions = prescriptionRepository
                .findByAppointmentIdOrderByCreatedDtDesc(appointmentId);

        // Group by creation time to identify prescription sets
        return prescriptions.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCreatedDt().withNano(0).withSecond(0)  // Group by minute
                ))
                .entrySet().stream()
                .map(entry -> {
                    LocalDateTime createdAt = entry.getKey();
                    List<Prescription> group = entry.getValue();
                    Prescription first = group.get(0);

                    // Count associated lab orders
                    int labTestCount = (int) labTestOrderRepository
                            .findByAppointmentIdOrderByOrderedAtDesc(appointmentId)
                            .stream()
                            .filter(lab -> lab.getOrderedAt() != null &&
                                    Math.abs(lab.getOrderedAt().toLocalTime().toSecondOfDay() -
                                            createdAt.toLocalTime().toSecondOfDay()) < 60)
                            .count();

                    return PrescriptionHistoryItem.builder()
                            .id(first.getId())
                            .appointmentId(appointmentId)
                            .prescriptionType(first.getPrescriptionType() != null ?
                                    first.getPrescriptionType().toString() : "FINAL")
                            .createdAt(createdAt)
                            .doctorName("Dr. " + first.getDoctor().getName())
                            .medicationCount(group.size())
                            .labTestOrderCount(labTestCount)
                            .status(first.getStatus().toString())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getPrescriptionsByDoctorAndHospital(Long doctorId, Long hospitalId, Pageable pageable) {
        log.info("Fetching prescriptions for doctor {} in hospital {}", doctorId, hospitalId);

        // Validate doctor
        validateDoctor(doctorId);

        // Validate hospital
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException("Hospital not found with ID: " + hospitalId);
        }

        List<Prescription> prescriptions = prescriptionRepository
                .findByDoctorAndHospital(doctorId, hospitalId, pageable)
                .getContent();

        return prescriptions.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Helper methods

    private void validateDoctor(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));

        if (doctor.getRole() != User.UserRole.DOCTOR) {
            throw new IllegalArgumentException("User with ID " + doctorId + " is not a doctor");
        }

        if (!doctor.getIsActive() || doctor.getStatus() != User.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Doctor with ID " + doctorId + " is not active");
        }
    }

    private PrescriptionResponseDto mapToResponseDto(Prescription prescription) {
        return PrescriptionResponseDto.builder()
                .id(prescription.getId())
                .patientId(prescription.getPatient().getId())
                .patientName(prescription.getPatient().getFirstName() + " " + prescription.getPatient().getLastName())
                .appointmentId(prescription.getAppointment() != null ? prescription.getAppointment().getId() : null)
                .doctorId(prescription.getDoctor().getId())
                .doctorName(prescription.getDoctor().getName())
                .medicationName(prescription.getMedicationName())
                .dosage(prescription.getDosage())
                .frequency(prescription.getFrequency())
                .duration(prescription.getDuration())
                .instructions(prescription.getInstructions())
                .notes(prescription.getNotes())
                .prescribedDate(prescription.getPrescriptionDate().atStartOfDay())
                .status(prescription.getStatus().getDisplayName())
                .refillsRemaining(prescription.getRefillsAllowed())
                .createdAt(prescription.getCreatedDt())
                .updatedAt(prescription.getLastUpdatedDt())
                .build();
    }
}
