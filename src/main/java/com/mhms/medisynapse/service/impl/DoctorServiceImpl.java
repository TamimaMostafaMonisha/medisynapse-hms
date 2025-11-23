package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.AppointmentDetailDto;
import com.mhms.medisynapse.dto.AppointmentDetailsResponseDto;
import com.mhms.medisynapse.dto.DoctorAppointmentDto;
import com.mhms.medisynapse.dto.DoctorAppointmentsResponseDto;
import com.mhms.medisynapse.dto.DoctorDashboardStatisticsDto;
import com.mhms.medisynapse.dto.DoctorPatientDto;
import com.mhms.medisynapse.dto.DoctorPatientsResponseDto;
import com.mhms.medisynapse.dto.DoctorProfileResponseDto;
import com.mhms.medisynapse.dto.EmergencyContactDto;
import com.mhms.medisynapse.dto.LabTestOrderResponse;
import com.mhms.medisynapse.dto.PatientDetailDto;
import com.mhms.medisynapse.dto.PrescriptionDto;
import com.mhms.medisynapse.dto.PreviousAppointmentDto;
import com.mhms.medisynapse.dto.UpdateAppointmentStatusRequest;
import com.mhms.medisynapse.dto.VitalSignsDto;
import com.mhms.medisynapse.entity.Admission;
import com.mhms.medisynapse.entity.Appointment;
import com.mhms.medisynapse.entity.Patient;
import com.mhms.medisynapse.entity.Prescription;
import com.mhms.medisynapse.entity.User;
import com.mhms.medisynapse.exception.ResourceNotFoundException;
import com.mhms.medisynapse.repository.AdmissionRepository;
import com.mhms.medisynapse.repository.AppointmentRepository;
import com.mhms.medisynapse.repository.PatientRepository;
import com.mhms.medisynapse.repository.PrescriptionRepository;
import com.mhms.medisynapse.repository.UserRepository;
import com.mhms.medisynapse.service.DoctorService;
import com.mhms.medisynapse.service.LabTestOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DoctorServiceImpl implements DoctorService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final AdmissionRepository admissionRepository;
    private final LabTestOrderService labTestOrderService;

    @Override
    public DoctorDashboardStatisticsDto getDashboardStatistics(Long doctorId) {
        log.info("Fetching dashboard statistics for doctor ID: {}", doctorId);

        validateDoctor(doctorId);

        Long totalPatients = appointmentRepository.countDistinctPatientsByDoctor(doctorId);
        Long todayAppointments = appointmentRepository.countTodayAppointmentsByDoctor(doctorId);
        Long upcomingAppointments = appointmentRepository.countUpcomingAppointmentsByDoctor(doctorId);
        Long completedAppointments = appointmentRepository.countCompletedAppointmentsByDoctor(doctorId);
        Long pendingAppointments = appointmentRepository.countPendingAppointmentsByDoctor(doctorId);

        log.info("Dashboard stats for doctor {}: {} patients, {} today, {} upcoming",
                doctorId, totalPatients, todayAppointments, upcomingAppointments);

        return DoctorDashboardStatisticsDto.builder()
                .totalPatients(totalPatients)
                .todayAppointments(todayAppointments)
                .upcomingAppointments(upcomingAppointments)
                .completedAppointments(completedAppointments)
                .pendingAppointments(pendingAppointments)
                .build();
    }

    @Override
    public DoctorPatientsResponseDto getPatients(Long doctorId, Patient.PatientStatus status,
                                                 String search, Pageable pageable) {
        log.info("Fetching patients for doctor ID: {} with status: {}, search: {}",
                doctorId, status, search);

        Page<Patient> patientPage;
        if (doctorId != null) {
            validateDoctor(doctorId);
            patientPage = patientRepository.findPatientsByDoctor(doctorId, status, search, pageable);
        } else {
            patientPage = patientRepository.findAllPatientsWithFilters(status, search, pageable);
        }

        List<DoctorPatientDto> patientDtos = patientPage.getContent().stream()
                .map(patient -> mapToPatientDto(patient, doctorId))
                .collect(Collectors.toList());

        return DoctorPatientsResponseDto.builder()
                .patients(patientDtos)
                .totalElements(patientPage.getTotalElements())
                .totalPages(patientPage.getTotalPages())
                .currentPage(patientPage.getNumber())
                .pageSize(patientPage.getSize())
                .build();
    }

    @Override
    public DoctorAppointmentsResponseDto getAppointments(Long doctorId, Appointment.AppointmentStatus status,
                                                         LocalDate date, LocalDate startDate,
                                                         LocalDate endDate, Pageable pageable) {
        log.info("Fetching appointments for doctor ID: {} with filters - status: {}, date: {}, range: {} to {}",
                doctorId, status, date, startDate, endDate);

        validateDoctor(doctorId);

        // If specific date is provided, override start and end dates
        if (date != null) {
            startDate = date;
            endDate = date;
        }

        Page<Appointment> appointmentPage = appointmentRepository.findAppointmentsByDoctorWithFilters(
                doctorId, status, startDate, endDate, pageable);

        List<DoctorAppointmentDto> appointmentDtos = appointmentPage.getContent().stream()
                .map(this::mapToAppointmentDto)
                .collect(Collectors.toList());

        return DoctorAppointmentsResponseDto.builder()
                .appointments(appointmentDtos)
                .totalElements(appointmentPage.getTotalElements())
                .totalPages(appointmentPage.getTotalPages())
                .currentPage(appointmentPage.getNumber())
                .pageSize(appointmentPage.getSize())
                .build();
    }

    @Override
    public List<DoctorAppointmentDto> getTodayAppointments(Long doctorId) {
        log.info("Fetching today's appointments for doctor ID: {}", doctorId);

        validateDoctor(doctorId);

        List<Appointment> appointments = appointmentRepository.findTodayAppointmentsByDoctor(doctorId);

        log.info("Found {} appointments for today for doctor ID: {}", appointments.size(), doctorId);

        return appointments.stream()
                .map(this::mapToAppointmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorAppointmentDto> getUpcomingAppointments(Long doctorId, Integer days) {
        log.info("Fetching upcoming appointments for doctor ID: {} for next {} days", doctorId, days);

        validateDoctor(doctorId);

        if (days == null || days <= 0) {
            days = 7; // Default to 7 days
        }

        // Start from tomorrow
        LocalDateTime startDate = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime endDate = LocalDate.now().plusDays(days + 1).atStartOfDay();

        List<Appointment> appointments = appointmentRepository.findUpcomingAppointmentsByDoctor(
                doctorId, startDate, endDate);

        log.info("Found {} upcoming appointments for doctor ID: {}", appointments.size(), doctorId);

        return appointments.stream()
                .map(this::mapToAppointmentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DoctorAppointmentDto updateAppointmentStatus(Long appointmentId, Long doctorId,
                                                        UpdateAppointmentStatusRequest request) {
        log.info("Updating appointment {} status to {} for doctor ID: {}",
                appointmentId, request.getStatus(), doctorId);

        validateDoctor(doctorId);

        Appointment appointment = appointmentRepository.findByIdAndDoctorId(appointmentId, doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with ID: " + appointmentId + " for doctor: " + doctorId));

        // Update status
        appointment.setStatus(request.getStatus());

        // Update notes if provided
        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            String existingNotes = appointment.getNotes() != null ? appointment.getNotes() : "";
            appointment.setNotes(existingNotes +
                    (existingNotes.isEmpty() ? "" : "\n") +
                    request.getNotes());
        }

        // Set timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        switch (request.getStatus()) {
            case COMPLETED:
                appointment.setCompletedAt(now);
                break;
            case CANCELLED:
                appointment.setCancelledAt(now);
                if (request.getNotes() != null) {
                    appointment.setCancellationReason(request.getNotes());
                }
                break;
            case IN_PROGRESS:
                if (appointment.getCheckedInAt() == null) {
                    appointment.setCheckedInAt(now);
                }
                break;
            default:
                break;
        }

        appointment.setUpdatedBy(doctorId);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        log.info("Successfully updated appointment {} to status {}", appointmentId, request.getStatus());

        return mapToAppointmentDto(savedAppointment);
    }

    @Override
    public List<DoctorPatientDto> getRecentPatients(Long doctorId, Integer limit) {
        log.info("Fetching recent patients for doctor ID: {} with limit: {}", doctorId, limit);

        validateDoctor(doctorId);

        if (limit == null || limit <= 0) {
            limit = 5; // Default to 5 patients
        }

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "lastUpdatedDt"));
        List<Patient> patients = patientRepository.findRecentPatientsByDoctor(doctorId, pageable);

        log.info("Found {} recent patients for doctor ID: {}", patients.size(), doctorId);

        return patients.stream()
                .map(patient -> mapToPatientDto(patient, doctorId))
                .collect(Collectors.toList());
    }

    @Override
    public DoctorProfileResponseDto getDoctorProfile(Long doctorId) {
        log.info("Fetching profile for doctor ID: {}", doctorId);

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));

        if (doctor.getRole() != User.UserRole.DOCTOR) {
            throw new IllegalArgumentException("User with ID " + doctorId + " is not a doctor");
        }
        if (!doctor.getIsActive() || doctor.getStatus() != User.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Doctor with ID " + doctorId + " is not active");
        }

        Long patientCount = appointmentRepository.countDistinctPatientsByDoctor(doctorId);
        Long todayAppointments = appointmentRepository.countTodayAppointmentsByDoctor(doctorId);
        Long upcomingAppointments = appointmentRepository.countUpcomingAppointmentsByDoctor(doctorId);
        Long completedAppointments = appointmentRepository.countCompletedAppointmentsByDoctor(doctorId);
        Long pendingAppointments = appointmentRepository.countPendingAppointmentsByDoctor(doctorId);
        Long totalAppointments = completedAppointments + pendingAppointments; // excludes historical cancelled/no-show

        return DoctorProfileResponseDto.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .role(doctor.getRole().name())
                .status(doctor.getStatus().name())
                .isActive(doctor.getIsActive())
                .hospitalId(doctor.getHospital() != null ? doctor.getHospital().getId() : null)
                .hospitalName(doctor.getHospital() != null ? doctor.getHospital().getName() : null)
                .departmentId(doctor.getDepartment() != null ? doctor.getDepartment().getId() : null)
                .departmentName(doctor.getDepartment() != null ? doctor.getDepartment().getName() : null)
                .appointmentCount(totalAppointments)
                .completedAppointmentCount(completedAppointments)
                .pendingAppointmentCount(pendingAppointments)
                .todayAppointmentCount(todayAppointments)
                .upcomingAppointmentCount(upcomingAppointments)
                .patientCount(patientCount)
                .createdAt(doctor.getCreatedDt())
                .updatedAt(doctor.getLastUpdatedDt())
                .build();
    }

    // Helper Methods

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

    private DoctorPatientDto mapToPatientDto(Patient patient, Long doctorId) {
        // Get current admissions if exist
        List<Admission> currentAdmissions = admissionRepository.findCurrentAdmissionByPatientId(patient.getId());
        Admission currentAdmission = currentAdmissions.isEmpty() ? null : currentAdmissions.get(0);

        // Get last visit date
        LocalDateTime lastVisitDate = appointmentRepository.findLastVisitDateByPatientAndDoctor(
                patient.getId(), doctorId);

        // Calculate age from DOB
        Integer age = null;
        if (patient.getDob() != null) {
            age = Period.between(patient.getDob(), LocalDate.now()).getYears();
        }

        // Get full address string
        String addressString = null;
        if (patient.getAddress() != null) {
            addressString = String.format("%s, %s, %s",
                    patient.getAddress().getLine1() != null ? patient.getAddress().getLine1() : "",
                    patient.getAddress().getCity() != null ? patient.getAddress().getCity() : "",
                    patient.getAddress().getState() != null ? patient.getAddress().getState() : "");
        }

        DoctorPatientDto.DoctorPatientDtoBuilder builder = DoctorPatientDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .fullName(patient.getFirstName() + " " + patient.getLastName())
                .age(age)
                .gender(patient.getGender())
                .phoneNumber(patient.getContact())
                .email(patient.getEmail())
                .address(addressString)
                .bloodGroup(patient.getBloodGroup())
                .assignedDoctorId(doctorId)
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactRelation(patient.getEmergencyContactRelation())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .medicalHistory(patient.getMedicalHistory())
                .lastVisitDate(lastVisitDate)
                .createdAt(patient.getCreatedDt())
                .updatedAt(patient.getLastUpdatedDt());

        // Add admission details if patient is currently admitted
        if (currentAdmission != null) {
            builder.admissionDate(currentAdmission.getCreatedDt())
                    .roomNumber(currentAdmission.getBedNo())
                    .status("ADMITTED")
                    .hospitalId(currentAdmission.getHospital().getId());
        } else {
            builder.status(patient.getStatus().name());
        }

        return builder.build();
    }

    private DoctorAppointmentDto mapToAppointmentDto(Appointment appointment) {
        String patientName = appointment.getPatient().getFirstName() + " " +
                appointment.getPatient().getLastName();

        String doctorName = appointment.getDoctor().getName();

        // Format date and time separately
        String appointmentDate = appointment.getStartTime().format(DATE_FORMATTER);
        String appointmentTime = appointment.getStartTime().format(TIME_FORMATTER);

        return DoctorAppointmentDto.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(patientName)
                .doctorId(appointment.getDoctor().getId())
                .doctorName(doctorName)
                .hospitalId(appointment.getHospital().getId())
                .appointmentDate(appointmentDate)
                .appointmentTime(appointmentTime)
                .duration(appointment.getDurationMinutes())
                .appointmentType(appointment.getAppointmentType())
                .status(appointment.getStatus())
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedDt())
                .build();
    }

    @Override
    public AppointmentDetailsResponseDto getAppointmentDetails(Long appointmentId, Long doctorId) {
        log.info("Fetching appointment details for appointment ID: {} and doctor ID: {}", appointmentId, doctorId);

        validateDoctor(doctorId);

        // Get appointment with patient and doctor details
        Appointment appointment = appointmentRepository.findAppointmentDetailsById(appointmentId, doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with ID: " + appointmentId + " for doctor: " + doctorId));

        Patient patient = appointment.getPatient();

        // Build appointment detail
        AppointmentDetailDto appointmentDetail = AppointmentDetailDto.builder()
                .id(appointment.getId())
                .patientId(patient.getId())
                .doctorId(appointment.getDoctor().getId())
                .hospitalId(appointment.getHospital().getId())
                .date(appointment.getStartTime().toLocalDate())
                .time(appointment.getStartTime().toLocalTime())
                .duration(appointment.getDurationMinutes())
                .type(formatAppointmentType(appointment.getAppointmentType()))
                .status(formatAppointmentStatus(appointment.getStatus()))
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedDt())
                .build();

        // Build patient detail
        PatientDetailDto patientDetail = buildPatientDetail(patient, doctorId);

        // Get medical history
        List<String> medicalHistory = parseMedicalHistory(patient.getMedicalHistory());

        // Get previous appointments (excluding current one)
        List<PreviousAppointmentDto> previousAppointments = getPreviousAppointments(
                patient.getId(), doctorId, appointmentId);

        // Get prescriptions
        List<PrescriptionDto> prescriptions = getPrescriptions(patient.getId(), doctorId);

        // Get vital signs (from latest appointment or EHR)
        VitalSignsDto vitalSigns = getLatestVitalSigns(patient.getId(), doctorId);

        // NEW: Get lab test orders for this appointment
        List<LabTestOrderResponse> labTestOrders = labTestOrderService.getLabOrdersForAppointment(appointmentId);
        log.info("Found {} lab test orders for appointment {}", labTestOrders.size(), appointmentId);

        return AppointmentDetailsResponseDto.builder()
                .appointment(appointmentDetail)
                .patient(patientDetail)
                .medicalHistory(medicalHistory)
                .previousAppointments(previousAppointments)
                .prescriptions(prescriptions)
                .vitalSigns(vitalSigns)
                .labTestOrders(labTestOrders)  // NEW: Include lab test orders
                .build();
    }

    private PatientDetailDto buildPatientDetail(Patient patient, Long doctorId) {
        // Get current admissions if exist
        List<Admission> currentAdmissions = admissionRepository.findCurrentAdmissionByPatientId(patient.getId());
        Admission currentAdmission = currentAdmissions.isEmpty() ? null : currentAdmissions.get(0);

        // Calculate age
        Integer age = null;
        if (patient.getDob() != null) {
            age = Period.between(patient.getDob(), LocalDate.now()).getYears();
        }

        // Build address string
        String addressString = null;
        if (patient.getAddress() != null) {
            addressString = String.format("%s, %s, %s %s",
                    patient.getAddress().getLine1() != null ? patient.getAddress().getLine1() : "",
                    patient.getAddress().getCity() != null ? patient.getAddress().getCity() : "",
                    patient.getAddress().getState() != null ? patient.getAddress().getState() : "",
                    patient.getAddress().getPostalCode() != null ? patient.getAddress().getPostalCode() : "");
        }

        // Build emergency contact
        EmergencyContactDto emergencyContact = null;
        if (patient.getEmergencyContactName() != null) {
            emergencyContact = EmergencyContactDto.builder()
                    .name(patient.getEmergencyContactName())
                    .relation(patient.getEmergencyContactRelation())
                    .phone(patient.getEmergencyContactPhone())
                    .build();
        }

        // Determine status
        String status = "Outpatient";
        LocalDateTime admissionDate = null;
        String roomNumber = null;

        if (currentAdmission != null) {
            status = "Inpatient";
            admissionDate = currentAdmission.getAdmissionDate();
            roomNumber = currentAdmission.getBedNo();
        }

        return PatientDetailDto.builder()
                .id(patient.getId())
                .name(patient.getFirstName() + " " + patient.getLastName())
                .age(age)
                .gender(patient.getGender() != null ? patient.getGender().name() : null)
                .phone(patient.getContact())
                .email(patient.getEmail())
                .address(addressString)
                .bloodGroup(patient.getBloodGroup())
                .emergencyContact(emergencyContact)
                .admissionDate(admissionDate)
                .status(status)
                .roomNumber(roomNumber)
                .nationalId(patient.getNationalId())
                .build();
    }

    private List<String> parseMedicalHistory(String medicalHistoryText) {
        if (medicalHistoryText == null || medicalHistoryText.trim().isEmpty()) {
            return List.of();
        }
        // Split by common delimiters
        String[] items = medicalHistoryText.split("[,;\\n]");
        return java.util.Arrays.stream(items)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private List<PreviousAppointmentDto> getPreviousAppointments(Long patientId, Long doctorId, Long excludeAppointmentId) {
        Pageable pageable = PageRequest.of(0, 5); // Get last 5 appointments
        List<Appointment> appointments = appointmentRepository.findPreviousAppointmentsByPatientAndDoctor(
                patientId, doctorId, excludeAppointmentId, pageable);

        return appointments.stream()
                .map(a -> PreviousAppointmentDto.builder()
                        .id(a.getId())
                        .date(a.getStartTime().toLocalDate())
                        .type(formatAppointmentType(a.getAppointmentType()))
                        .status(formatAppointmentStatus(a.getStatus()))
                        .notes(a.getNotes())
                        .build())
                .collect(Collectors.toList());
    }

    private List<PrescriptionDto> getPrescriptions(Long patientId, Long doctorId) {
        List<Prescription> prescriptions = prescriptionRepository.findByPatientAndDoctor(patientId, doctorId);

        return prescriptions.stream()
                .limit(10) // Limit to recent 10 prescriptions
                .map(p -> {
                    // Calculate end date from prescription date and duration
                    LocalDate startDate = p.getPrescriptionDate();
                    LocalDate endDate = calculateEndDate(startDate, p.getDuration());

                    return PrescriptionDto.builder()
                            .id(p.getId())
                            .medicationName(p.getMedicationName())
                            .dosage(p.getDosage())
                            .frequency(p.getFrequency())
                            .startDate(startDate)
                            .endDate(endDate)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private LocalDate calculateEndDate(LocalDate startDate, String duration) {
        if (duration == null || duration.trim().isEmpty()) {
            return startDate.plusDays(30); // Default 30 days
        }

        // Parse duration like "30 days", "3 months", etc.
        try {
            String[] parts = duration.toLowerCase().trim().split("\\s+");
            if (parts.length >= 2) {
                int value = Integer.parseInt(parts[0]);
                String unit = parts[1];

                if (unit.startsWith("day")) {
                    return startDate.plusDays(value);
                } else if (unit.startsWith("week")) {
                    return startDate.plusWeeks(value);
                } else if (unit.startsWith("month")) {
                    return startDate.plusMonths(value);
                }
            }
        } catch (Exception e) {
            log.warn("Could not parse duration: {}", duration);
        }

        return startDate.plusDays(30); // Default
    }

    private VitalSignsDto getLatestVitalSigns(Long patientId, Long doctorId) {
        // For now, return null as vital signs data is not in the current schema
        // This would typically come from the latest appointment or EHR record
        return null;
    }

    private String formatAppointmentType(Appointment.AppointmentType type) {
        if (type == null) return null;

        // Convert ROUTINE_CHECKUP to "Check-up", etc.
        switch (type) {
            case ROUTINE_CHECKUP:
                return "Check-up";
            case SURGICAL_CONSULTATION:
                return "Surgical Consultation";
            case FOLLOW_UP:
                return "Follow-up";
            default:
                return type.name().substring(0, 1).toUpperCase() +
                        type.name().substring(1).toLowerCase();
        }
    }

    private String formatAppointmentStatus(Appointment.AppointmentStatus status) {
        if (status == null) return null;

        // Convert to proper case
        return status.name().substring(0, 1).toUpperCase() +
                status.name().substring(1).toLowerCase().replace("_", " ");
    }

    @Override
    public DoctorAppointmentDto completeAppointment(Long appointmentId, Long doctorId) {
        log.info("Marking appointment {} as completed by doctor {}", appointmentId, doctorId);

        // Validate doctor
        validateDoctor(doctorId);

        // Find appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        // Verify appointment belongs to doctor
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("Appointment does not belong to this doctor");
        }

        // Check if appointment can be completed
        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            log.warn("Appointment {} is already completed", appointmentId);
            throw new IllegalArgumentException("Appointment is already completed");
        }

        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot complete a cancelled appointment");
        }

        if (appointment.getStatus() == Appointment.AppointmentStatus.NO_SHOW) {
            throw new IllegalArgumentException("Cannot complete a no-show appointment");
        }

        // Store old status for logging
        Appointment.AppointmentStatus oldStatus = appointment.getStatus();

        // Update appointment status to COMPLETED
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment.setCompletedAt(java.time.LocalDateTime.now());
        appointment.setUpdatedBy(doctorId);

        Appointment completedAppointment = appointmentRepository.save(appointment);
        appointmentRepository.flush(); // Force immediate persistence

        log.info("Appointment {} status changed from {} to COMPLETED successfully",
                appointmentId, oldStatus);

        return mapToAppointmentDto(completedAppointment);
    }
}

