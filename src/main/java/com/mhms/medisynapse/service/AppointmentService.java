package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.AppointmentDto;
import com.mhms.medisynapse.dto.AppointmentFilterDto;
import com.mhms.medisynapse.dto.CreateAppointmentRequestDto;
import com.mhms.medisynapse.dto.DoctorAvailabilityDto;
import com.mhms.medisynapse.dto.UpdateAppointmentRequestDto;
import com.mhms.medisynapse.entity.Address;
import com.mhms.medisynapse.entity.Appointment;
import com.mhms.medisynapse.entity.Department;
import com.mhms.medisynapse.entity.Hospital;
import com.mhms.medisynapse.entity.Patient;
import com.mhms.medisynapse.entity.User;
import com.mhms.medisynapse.exception.BusinessLogicException;
import com.mhms.medisynapse.repository.AppointmentRepository;
import com.mhms.medisynapse.repository.DepartmentRepository;
import com.mhms.medisynapse.repository.HospitalRepository;
import com.mhms.medisynapse.repository.PatientRepository;
import com.mhms.medisynapse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private static final int DEFAULT_SLOT_INTERVAL_MINUTES = 30;
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(17, 0);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * Create a new appointment with comprehensive validation
     */
    public ApiResponse<AppointmentDto> createAppointment(CreateAppointmentRequestDto request, Long createdBy) {
        log.info("Creating appointment for patient {} with doctor {}", request.getPatientId(), request.getDoctorId());

        // Validate entities exist and belong to the same hospital
        validateAppointmentEntities(request);

        // Check doctor availability
        validateDoctorAvailability(request.getDoctorId(), request.getStartTime(),
                request.getDurationMinutes(), null);

        // Create appointment entity
        Appointment appointment = buildAppointmentEntity(request, createdBy);

        // Save appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);


        log.info("Successfully created appointment with ID: {}", savedAppointment.getId());
        return ApiResponse.<AppointmentDto>builder()
                .success(true)
                .message("Appointment created successfully")
                .data(convertToDto(savedAppointment))
                .build();
    }

    /**
     * Update/reschedule appointment with conflict validation
     */
    public ApiResponse<AppointmentDto> updateAppointment(Long appointmentId, UpdateAppointmentRequestDto request,
                                                         Long hospitalId, Long updatedBy) {
        log.info("Updating appointment {}", appointmentId);

        Appointment appointment = appointmentRepository.findByIdAndHospitalId(appointmentId, hospitalId)
                .orElseThrow(() -> new BusinessLogicException("Appointment not found"));

        // If rescheduling, validate new time slot
        if (request.getStartTime() != null) {
            validateDoctorAvailability(appointment.getDoctor().getId(), request.getStartTime(),
                    request.getDurationMinutes() != null ? request.getDurationMinutes() : appointment.getDurationMinutes(),
                    appointmentId);
        }

        // Update appointment fields
        updateAppointmentFields(appointment, request, updatedBy);

        Appointment updatedAppointment = appointmentRepository.save(appointment);


        log.info("Successfully updated appointment {}", appointmentId);
        return ApiResponse.<AppointmentDto>builder()
                .success(true)
                .message("Appointment updated successfully")
                .data(convertToDto(updatedAppointment))
                .build();
    }

    /**
     * Cancel appointment and free up time slot
     */
    public ApiResponse<Void> cancelAppointment(Long appointmentId, String cancellationReason,
                                               Long hospitalId, Long updatedBy) {
        log.info("Cancelling appointment {}", appointmentId);

        Appointment appointment = appointmentRepository.findByIdAndHospitalId(appointmentId, hospitalId)
                .orElseThrow(() -> new BusinessLogicException("Appointment not found"));

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(cancellationReason);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setUpdatedBy(updatedBy);

        appointmentRepository.save(appointment);

        log.info("Successfully cancelled appointment {}", appointmentId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Appointment cancelled successfully")
                .build();
    }

    /**
     * Get appointments with comprehensive filtering and pagination
     */
    @Transactional(readOnly = true)
    public ApiResponse<Page<AppointmentDto>> getAppointments(AppointmentFilterDto filter) {
        log.info("Fetching appointments with filters: {}", filter);

        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDirection()), filter.getSortBy());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        // Convert LocalDate to LocalDateTime for repository
        LocalDateTime startDateTime = filter.getStartDate() != null ? filter.getStartDate().atStartOfDay() : null;
        LocalDateTime endDateTime = filter.getEndDate() != null ? filter.getEndDate().atTime(java.time.LocalTime.MAX) : null;

        Page<Appointment> appointments = appointmentRepository.findAppointmentsWithFilters(
                filter.getHospitalId(),
                filter.getDoctorId(),
                filter.getPatientId(),
                filter.getDepartmentId(),
                filter.getAppointmentType(),
                startDateTime,
                endDateTime,
                filter.getStatus(), // now a List<AppointmentStatus>
                pageable
        );

        Page<AppointmentDto> appointmentDtos = appointments.map(this::convertToDto);

        return ApiResponse.<Page<AppointmentDto>>builder()
                .success(true)
                .message("Appointments retrieved successfully")
                .data(appointmentDtos)
                .build();
    }

    /**
     * Get doctor availability with time slots
     */
    @Cacheable(value = "doctorAvailability", key = "#doctorId + '_' + #date")
    @Transactional(readOnly = true)
    public ApiResponse<DoctorAvailabilityDto> getDoctorAvailability(Long doctorId, LocalDate date,
                                                                    Integer slotIntervalMinutes) {
        log.info("Getting availability for doctor {} on {}", doctorId, date);

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessLogicException("Doctor not found"));

        if (slotIntervalMinutes == null) {
            slotIntervalMinutes = DEFAULT_SLOT_INTERVAL_MINUTES;
        }

        // Get existing appointments for the date
        List<Appointment> existingAppointments = appointmentRepository.findAppointmentsByDoctorAndDate(doctorId, date);

        // Generate time slots
        List<DoctorAvailabilityDto.TimeSlotDto> availableSlots = generateTimeSlots(date, slotIntervalMinutes, existingAppointments);
        List<DoctorAvailabilityDto.TimeSlotDto> bookedSlots = generateBookedSlots(existingAppointments);

        DoctorAvailabilityDto availability = DoctorAvailabilityDto.builder()
                .doctorId(doctorId)
                .doctorName(doctor.getName())
                .date(date)
                .availableSlots(availableSlots)
                .bookedSlots(bookedSlots)
                .workingHours(DEFAULT_START_TIME + " - " + DEFAULT_END_TIME)
                .build();

        return ApiResponse.<DoctorAvailabilityDto>builder()
                .success(true)
                .message("Doctor availability retrieved successfully")
                .data(availability)
                .build();
    }

    /**
     * Get upcoming appointments for dashboard
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<AppointmentDto>> getUpcomingAppointments(Long hospitalId, Integer days) {
        log.info("Getting upcoming appointments for hospital {} for next {} days", hospitalId, days);

        LocalDateTime fromTime = LocalDateTime.now();
        LocalDateTime toTime = fromTime.plusDays(days != null ? days : 7);

        List<Appointment> appointments = appointmentRepository.findUpcomingAppointments(hospitalId, fromTime, toTime);
        List<AppointmentDto> appointmentDtos = appointments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ApiResponse.<List<AppointmentDto>>builder()
                .success(true)
                .message("Upcoming appointments retrieved successfully")
                .data(appointmentDtos)
                .build();
    }

    /**
     * Get today's appointments for a doctor
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<AppointmentDto>> getTodayAppointmentsByDoctor(Long doctorId) {
        log.info("Getting today's appointments for doctor {}", doctorId);

        List<Appointment> appointments = appointmentRepository.findTodayAppointmentsByDoctor(doctorId);
        List<AppointmentDto> appointmentDtos = appointments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ApiResponse.<List<AppointmentDto>>builder()
                .success(true)
                .message("Today's appointments retrieved successfully")
                .data(appointmentDtos)
                .build();
    }

    /**
     * Get patient appointment history
     */
    @Transactional(readOnly = true)
    public ApiResponse<Page<AppointmentDto>> getPatientAppointmentHistory(Long patientId, Long hospitalId,
                                                                          int page, int size) {
        log.info("Getting appointment history for patient {} in hospital {}", patientId, hospitalId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<Appointment> appointments = appointmentRepository.findPatientAppointmentHistory(patientId, hospitalId, pageable);
        Page<AppointmentDto> appointmentDtos = appointments.map(this::convertToDto);

        return ApiResponse.<Page<AppointmentDto>>builder()
                .success(true)
                .message("Patient appointment history retrieved successfully")
                .data(appointmentDtos)
                .build();
    }

    // Private helper methods

    private void validateAppointmentEntities(CreateAppointmentRequestDto request) {
        // Validate patient exists
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new BusinessLogicException("Patient not found"));

        // Validate doctor exists
        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new BusinessLogicException("Doctor not found"));

        // Validate hospital exists
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new BusinessLogicException("Hospital not found"));

        // Validate doctor belongs to the hospital
        if (!doctor.getHospital().getId().equals(request.getHospitalId())) {
            throw new BusinessLogicException("Doctor does not belong to the specified hospital");
        }

        // Set department from doctor if not provided
        if (request.getDepartmentId() == null && doctor.getDepartment() != null) {
            request.setDepartmentId(doctor.getDepartment().getId());
        }
    }

    private void validateDoctorAvailability(Long doctorId, LocalDateTime startTime, Integer durationMinutes, Long excludeAppointmentId) {
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        Long conflictCount = appointmentRepository.countConflictingAppointments(
                doctorId, startTime, endTime, excludeAppointmentId != null ? excludeAppointmentId : 0L);

        if (conflictCount > 0) {
            throw new BusinessLogicException("Doctor is not available at the requested time slot");
        }
    }

    private Appointment buildAppointmentEntity(CreateAppointmentRequestDto request, Long createdBy) {
        Patient patient = patientRepository.findById(request.getPatientId()).orElseThrow();
        User doctor = userRepository.findById(request.getDoctorId()).orElseThrow();
        Hospital hospital = hospitalRepository.findById(request.getHospitalId()).orElseThrow();
        Department department = request.getDepartmentId() != null ?
                departmentRepository.findById(request.getDepartmentId()).orElse(doctor.getDepartment()) :
                doctor.getDepartment();

        return Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .hospital(hospital)
                .department(department)
                .startTime(request.getStartTime())
                .durationMinutes(request.getDurationMinutes())
                .appointmentType(request.getAppointmentType())
                .reason(request.getReason())
                .notes(request.getNotes())
                .isRecurring(request.getIsRecurring())
                .recurringPattern(request.getRecurringPattern())
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .isActive(true)
                .build();
    }

    private void updateAppointmentFields(Appointment appointment, UpdateAppointmentRequestDto request, Long updatedBy) {
        if (request.getStartTime() != null) {
            appointment.setStartTime(request.getStartTime());
        }
        if (request.getDurationMinutes() != null) {
            appointment.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getAppointmentType() != null) {
            appointment.setAppointmentType(request.getAppointmentType());
        }
        if (request.getStatus() != null) {
            appointment.setStatus(request.getStatus());
            if (request.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
                appointment.setCompletedAt(LocalDateTime.now());
            }
        }
        if (request.getReason() != null) {
            appointment.setReason(request.getReason());
        }
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }
        if (request.getCancellationReason() != null) {
            appointment.setCancellationReason(request.getCancellationReason());
        }
        if (request.getIsRecurring() != null) {
            appointment.setIsRecurring(request.getIsRecurring());
        }
        if (request.getRecurringPattern() != null) {
            appointment.setRecurringPattern(request.getRecurringPattern());
        }

        appointment.setUpdatedBy(updatedBy);
    }

    private List<DoctorAvailabilityDto.TimeSlotDto> generateTimeSlots(LocalDate date, Integer slotIntervalMinutes,
                                                                      List<Appointment> existingAppointments) {
        List<DoctorAvailabilityDto.TimeSlotDto> slots = new ArrayList<>();
        LocalDateTime currentSlot = date.atTime(DEFAULT_START_TIME);
        LocalDateTime endOfDay = date.atTime(DEFAULT_END_TIME);

        while (currentSlot.isBefore(endOfDay)) {
            LocalDateTime slotStartFinal = currentSlot;
            LocalDateTime slotEndFinal = currentSlot.plusMinutes(slotIntervalMinutes);

            boolean isAvailable = existingAppointments.stream()
                    .noneMatch(appointment ->
                            appointment.getStartTime().isBefore(slotEndFinal) &&
                                    appointment.getEndTime().isAfter(slotStartFinal));

            slots.add(DoctorAvailabilityDto.TimeSlotDto.builder()
                    .startTime(slotStartFinal.format(TIME_FORMATTER))
                    .endTime(slotEndFinal.format(TIME_FORMATTER))
                    .available(isAvailable)
                    .reason(isAvailable ? null : "Booked")
                    .build());

            currentSlot = slotEndFinal;
        }

        return slots;
    }

    private List<DoctorAvailabilityDto.TimeSlotDto> generateBookedSlots(List<Appointment> appointments) {
        return appointments.stream()
                .map(appointment -> DoctorAvailabilityDto.TimeSlotDto.builder()
                        .startTime(appointment.getStartTime().format(TIME_FORMATTER))
                        .endTime(appointment.getEndTime().format(TIME_FORMATTER))
                        .available(false)
                        .reason("Booked - Appointment #" + appointment.getId())
                        .appointmentId(appointment.getId())
                        .patientName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName())
                        .build())
                .collect(Collectors.toList());
    }

    private AppointmentDto convertToDto(Appointment appointment) {
        Patient patient = appointment.getPatient();
        User doctor = appointment.getDoctor();

        return AppointmentDto.builder()
                .id(appointment.getId())

                // Comprehensive Patient Details
                .patientId(patient.getId())
                .patientFirstName(patient.getFirstName())
                .patientLastName(patient.getLastName())
                .patientFullName(patient.getFirstName() + " " + patient.getLastName())
                .patientNationalId(patient.getNationalId())
                .patientDob(patient.getDob())
                .patientGender(patient.getGender())
                .patientContact(patient.getContact())
                .patientEmail(patient.getEmail())
                .patientBloodGroup(patient.getBloodGroup())
                .patientMedicalHistory(patient.getMedicalHistory())
                .patientEmergencyContactName(patient.getEmergencyContactName())
                .patientEmergencyContactRelation(patient.getEmergencyContactRelation())
                .patientEmergencyContactPhone(patient.getEmergencyContactPhone())
                .patientStatus(patient.getStatus())
                .patientAddress(patient.getAddress() != null ?
                        formatAddress(patient.getAddress()) : null)

                // Doctor Details
                .doctorId(doctor.getId())
                .doctorName(doctor.getName())
                .doctorContact(doctor.getPhone())
                .doctorEmail(doctor.getEmail())
                .doctorSpecialty(appointment.getDepartment() != null ?
                        appointment.getDepartment().getName() : "General")

                // Department Details
                .departmentId(appointment.getDepartment() != null ? appointment.getDepartment().getId() : null)
                .departmentName(appointment.getDepartment() != null ? appointment.getDepartment().getName() : null)

                // Hospital Details
                .hospitalId(appointment.getHospital().getId())
                .hospitalName(appointment.getHospital().getName())

                // Appointment Details
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .durationMinutes(appointment.getDurationMinutes())
                .appointmentType(appointment.getAppointmentType())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .reason(appointment.getReason())
                .cancellationReason(appointment.getCancellationReason())

                // Reminder and Status Tracking
                .reminderSent(appointment.getReminderSent())
                .reminderSentAt(appointment.getReminderSentAt())
                .checkedInAt(appointment.getCheckedInAt())
                .completedAt(appointment.getCompletedAt())
                .cancelledAt(appointment.getCancelledAt())

                // Recurring Appointment Details
                .isRecurring(appointment.getIsRecurring())
                .recurringPattern(appointment.getRecurringPattern())
                .parentAppointmentId(appointment.getParentAppointmentId())

                // Audit Fields
                .createdDt(appointment.getCreatedDt())
                .lastUpdatedDt(appointment.getLastUpdatedDt())
                .createdBy(appointment.getCreatedBy())
                .updatedBy(appointment.getUpdatedBy())
                .version(appointment.getVersion())
                .build();
    }

    private String formatAddress(Address address) {
        StringBuilder addressStr = new StringBuilder();

        if (address.getLine1() != null && !address.getLine1().isEmpty()) {
            addressStr.append(address.getLine1());
        }
        if (address.getLine2() != null && !address.getLine2().isEmpty()) {
            if (addressStr.length() > 0) addressStr.append(", ");
            addressStr.append(address.getLine2());
        }
        if (address.getCity() != null && !address.getCity().isEmpty()) {
            if (addressStr.length() > 0) addressStr.append(", ");
            addressStr.append(address.getCity());
        }
        if (address.getState() != null && !address.getState().isEmpty()) {
            if (addressStr.length() > 0) addressStr.append(", ");
            addressStr.append(address.getState());
        }
        if (address.getPostalCode() != null && !address.getPostalCode().isEmpty()) {
            if (addressStr.length() > 0) addressStr.append(" ");
            addressStr.append(address.getPostalCode());
        }
        if (address.getCountry() != null && !address.getCountry().isEmpty()) {
            if (addressStr.length() > 0) addressStr.append(", ");
            addressStr.append(address.getCountry());
        }

        return addressStr.toString();
    }
}
