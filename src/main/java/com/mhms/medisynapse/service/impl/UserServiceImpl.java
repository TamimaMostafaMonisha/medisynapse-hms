package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.AvailableHospitalDto;
import com.mhms.medisynapse.dto.CreateHospitalAdminDto;
import com.mhms.medisynapse.dto.DoctorAvailabilityDto;
import com.mhms.medisynapse.dto.DoctorDto;
import com.mhms.medisynapse.dto.DoctorListResponseDto;
import com.mhms.medisynapse.dto.HospitalAdminListDto;
import com.mhms.medisynapse.dto.HospitalAdminPagedResponseDto;
import com.mhms.medisynapse.dto.HospitalAdminResponseDto;
import com.mhms.medisynapse.dto.PaginationInfo;
import com.mhms.medisynapse.dto.PasswordResetResponseDto;
import com.mhms.medisynapse.dto.ResetPasswordDto;
import com.mhms.medisynapse.dto.UpdateHospitalAdminDto;
import com.mhms.medisynapse.entity.Appointment;
import com.mhms.medisynapse.entity.Hospital;
import com.mhms.medisynapse.entity.User;
import com.mhms.medisynapse.exception.ResourceNotFoundException;
import com.mhms.medisynapse.exception.ValidationException;
import com.mhms.medisynapse.repository.AppointmentRepository;
import com.mhms.medisynapse.repository.HospitalRepository;
import com.mhms.medisynapse.repository.UserRepository;
import com.mhms.medisynapse.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRepository appointmentRepository;

    @Override
    public HospitalAdminResponseDto createHospitalAdmin(CreateHospitalAdminDto createDto) {
        log.info("Creating hospital admin with email: {}", createDto.getEmail());

        // Validation
        validateHospitalAdminCreation(createDto);

        // Get the hospital
        Hospital hospital = hospitalRepository.findById(createDto.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + createDto.getHospitalId()));

        // Create user entity
        User user = new User();
        user.setName(createDto.getName());
        user.setEmail(createDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(createDto.getPassword()));
        user.setPhone(createDto.getPhone());
        user.setNationalId(createDto.getNationalId());
        user.setRole(User.UserRole.HOSPITAL_ADMIN);
        user.setStatus(User.UserStatus.valueOf(createDto.getStatus()));
        user.setHospital(hospital);
        user.setIsActive(true);

        // Save user
        User savedUser = userRepository.save(user);

        log.info("Hospital admin created successfully with ID: {}", savedUser.getId());

        // Map to response DTO
        return HospitalAdminResponseDto.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .phone(savedUser.getPhone())
                .nationalId(savedUser.getNationalId())
                .hospitalId(savedUser.getHospital().getId())
                .hospitalName(savedUser.getHospital().getName())
                .status(savedUser.getStatus().name())
                .createdAt(savedUser.getCreatedDt())
                .lastUpdatedAt(savedUser.getLastUpdatedDt())
                .build();
    }

    private void validateHospitalAdminCreation(CreateHospitalAdminDto createDto) {
        Map<String, String> errors = new HashMap<>();

        // Check if email already exists
        if (userRepository.existsByEmail(createDto.getEmail())) {
            errors.put("email", "Email already exists in the system");
        }

        // Check if national ID already exists (if provided)
        if (createDto.getNationalId() != null && !createDto.getNationalId().trim().isEmpty()) {
            if (userRepository.existsByNationalId(createDto.getNationalId())) {
                errors.put("nationalId", "National ID already in use");
            }
        }

        // Check if phone number already exists (if provided)
        if (createDto.getPhone() != null && !createDto.getPhone().trim().isEmpty()) {
            if (userRepository.existsByPhone(createDto.getPhone())) {
                errors.put("phone", "Phone number already in use");
            }
        }

        // Check if hospital exists
        if (!hospitalRepository.existsById(createDto.getHospitalId())) {
            errors.put("hospitalId", "Invalid hospital ID");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }

    @Override
    public HospitalAdminResponseDto updateHospitalAdmin(Long id, UpdateHospitalAdminDto updateDto) {
        log.info("Updating hospital admin with ID: {}", id);

        // Find the existing hospital admin
        User existingUser = userRepository.findActiveUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital admin not found with id: " + id));

        // Validate that the user is actually a hospital admin
        if (existingUser.getRole() != User.UserRole.HOSPITAL_ADMIN) {
            throw new ResourceNotFoundException("Hospital admin not found with id: " + id);
        }

        // Validation for updates
        validateHospitalAdminUpdate(id, updateDto);

        // Update only provided fields (partial update)
        updateUserFields(existingUser, updateDto);

        // Save updated user
        User updatedUser = userRepository.save(existingUser);

        log.info("Hospital admin updated successfully with ID: {}", updatedUser.getId());

        // Map to response DTO
        return HospitalAdminResponseDto.builder()
                .id(updatedUser.getId())
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole().name())
                .phone(updatedUser.getPhone())
                .nationalId(updatedUser.getNationalId())
                .hospitalId(updatedUser.getHospital().getId())
                .hospitalName(updatedUser.getHospital().getName())
                .status(updatedUser.getStatus().name())
                .createdAt(updatedUser.getCreatedDt())
                .lastUpdatedAt(updatedUser.getLastUpdatedDt())
                .build();
    }

    private void validateHospitalAdminUpdate(Long userId, UpdateHospitalAdminDto updateDto) {
        Map<String, String> errors = new HashMap<>();

        // Check if email already exists (exclude current user)
        if (updateDto.getEmail() != null && !updateDto.getEmail().trim().isEmpty()) {
            if (userRepository.existsByEmailExcludingId(updateDto.getEmail(), userId)) {
                errors.put("email", "Email already exists in the system");
            }
        }

        // Check if national ID already exists (exclude current user)
        if (updateDto.getNationalId() != null && !updateDto.getNationalId().trim().isEmpty()) {
            if (userRepository.existsByNationalIdExcludingId(updateDto.getNationalId(), userId)) {
                errors.put("nationalId", "National ID already in use");
            }
        }

        // Check if phone number already exists (exclude current user)
        if (updateDto.getPhone() != null && !updateDto.getPhone().trim().isEmpty()) {
            if (userRepository.existsByPhoneExcludingId(updateDto.getPhone(), userId)) {
                errors.put("phone", "Phone number already in use");
            }
        }

        // Check if hospital exists (if hospital is being updated)
        if (updateDto.getHospitalId() != null) {
            if (!hospitalRepository.existsById(updateDto.getHospitalId())) {
                errors.put("hospitalId", "Invalid hospital ID");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }

    private void updateUserFields(User existingUser, UpdateHospitalAdminDto updateDto) {
        // Update only provided fields (partial update)
        if (updateDto.getName() != null && !updateDto.getName().trim().isEmpty()) {
            existingUser.setName(updateDto.getName().trim());
        }

        if (updateDto.getEmail() != null && !updateDto.getEmail().trim().isEmpty()) {
            existingUser.setEmail(updateDto.getEmail().trim());
        }

        if (updateDto.getPhone() != null) {
            existingUser.setPhone(updateDto.getPhone().trim().isEmpty() ? null : updateDto.getPhone().trim());
        }

        if (updateDto.getNationalId() != null) {
            existingUser.setNationalId(updateDto.getNationalId().trim().isEmpty() ? null : updateDto.getNationalId().trim());
        }

        if (updateDto.getStatus() != null && !updateDto.getStatus().trim().isEmpty()) {
            existingUser.setStatus(User.UserStatus.valueOf(updateDto.getStatus()));
        }

        if (updateDto.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(updateDto.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + updateDto.getHospitalId()));
            existingUser.setHospital(hospital);
        }

        // Note: Role cannot be changed (remains HOSPITAL_ADMIN)
        // Note: Password cannot be changed via this endpoint
        // Note: createdAt and createdBy are preserved
        // lastUpdatedDt and updatedBy will be automatically updated by @PreUpdate
    }

    @Override
    public PasswordResetResponseDto resetHospitalAdminPassword(Long id, ResetPasswordDto resetPasswordDto) {
        log.info("Resetting password for hospital admin with ID: {}", id);

        // Find the existing hospital admin
        User existingUser = userRepository.findActiveUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital admin not found with id: " + id));

        // Validate that the user is actually a hospital admin
        if (existingUser.getRole() != User.UserRole.HOSPITAL_ADMIN) {
            throw new ResourceNotFoundException("Hospital admin not found with id: " + id);
        }

        // Validate that the user is active
        if (existingUser.getStatus() != User.UserStatus.ACTIVE) {
            throw new ValidationException("Password reset failed",
                    Map.of("status", "Can only reset password for active hospital admins"));
        }

        // Hash the new password using BCrypt
        String hashedPassword = passwordEncoder.encode(resetPasswordDto.getNewPassword());

        // Update password and reset timestamp
        existingUser.setPasswordHash(hashedPassword);
        existingUser.setPasswordResetDt(LocalDateTime.now());

        // Save updated user
        User updatedUser = userRepository.save(existingUser);

        log.info("Password reset successfully for hospital admin with ID: {}", updatedUser.getId());

        // Map to response DTO (without returning the hashed password)
        return PasswordResetResponseDto.builder()
                .id(updatedUser.getId())
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .passwordResetAt(updatedUser.getPasswordResetDt())
                .build();
    }

    @Override
    public List<AvailableHospitalDto> getAvailableHospitals() {
        log.info("Fetching all available hospitals");

        // Get all active hospitals ordered by name
        List<Hospital> hospitals = hospitalRepository.findAllActiveHospitalsWithAddress();

        log.info("Found {} available hospitals", hospitals.size());

        // Map to DTO
        return hospitals.stream()
                .map(this::mapToAvailableHospitalDto)
                .toList();
    }

    @Override
    public HospitalAdminPagedResponseDto getHospitalAdmins(int page, int size, String sortBy, String sortDir, Long hospitalId) {
        log.info("Fetching hospital admins - page: {}, size: {}, sortBy: {}, sortDir: {}, hospitalId: {}",
                page, size, sortBy, sortDir, hospitalId);

        // Create pageable with sorting
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Fetch hospital admins with pagination
        Page<User> hospitalAdminsPage = userRepository.findHospitalAdmins(hospitalId, pageable);

        log.info("Found {} hospital admins (total: {})", hospitalAdminsPage.getNumberOfElements(), hospitalAdminsPage.getTotalElements());

        // Map to DTOs
        List<HospitalAdminListDto> hospitalAdminList = hospitalAdminsPage.getContent().stream()
                .map(this::mapToHospitalAdminListDto)
                .toList();

        // Create pagination info
        PaginationInfo paginationInfo = PaginationInfo.builder()
                .currentPage(hospitalAdminsPage.getNumber())
                .totalPages(hospitalAdminsPage.getTotalPages())
                .totalElements(hospitalAdminsPage.getTotalElements())
                .pageSize(hospitalAdminsPage.getSize())
                .hasNext(hospitalAdminsPage.hasNext())
                .hasPrevious(hospitalAdminsPage.hasPrevious())
                .build();

        return HospitalAdminPagedResponseDto.builder()
                .data(hospitalAdminList)
                .pagination(paginationInfo)
                .build();
    }

    // Doctor management methods - implementing only the requested APIs
    @Override
    public DoctorListResponseDto getDoctorsByHospital(Long hospitalId) {
        log.info("Fetching doctors for hospital ID: {}", hospitalId);

        // Check if hospital exists
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException("Hospital not found with ID: " + hospitalId);
        }

        // Fetch doctors associated with the hospital
        List<User> doctors = userRepository.findDoctorsByHospital(hospitalId);

        log.info("Found {} doctors for hospital ID: {}", doctors.size(), hospitalId);

        // Map to DTOs with additional information
        List<DoctorDto> doctorDtos = doctors.stream()
                .map(this::mapToDoctorDto)
                .toList();

        return DoctorListResponseDto.builder()
                .doctors(doctorDtos)
                .build();
    }

    @Override
    public DoctorAvailabilityDto getDoctorAvailability(Long doctorId, LocalDate date) {
        log.info("Fetching availability for doctor ID: {} on date: {}", doctorId, date);

        // Find the doctor
        User doctor = userRepository.findActiveUserById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        // Validate that the user is actually a doctor
        if (doctor.getRole() != User.UserRole.DOCTOR) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }

        // Fetch appointments for the doctor on the specified date
        List<Appointment> appointments = appointmentRepository.findAppointmentsByDoctorAndDate(doctorId, date);

        // Generate time slots (assuming 30-minute slots from 9 AM to 5 PM)
        List<DoctorAvailabilityDto.TimeSlotDto> timeSlots = generateTimeSlots(appointments);

        return DoctorAvailabilityDto.builder()
                .doctorId(doctorId)
                .date(date)
                .availableSlots(timeSlots)
                .build();
    }

    private DoctorDto mapToDoctorDto(User user) {
        // Get today's appointment count
        Long todayAppointments = userRepository.countTodayAppointmentsByDoctor(user.getId());

        // Create sample schedule (in real implementation, this would come from a schedule table)
        Map<String, DoctorDto.ScheduleDto> schedule = createDefaultSchedule();

        return DoctorDto.builder()
                .id(user.getId())
                .name(user.getName())
                .specialty(user.getDepartment() != null ? user.getDepartment().getName() : "General")
                .department(user.getDepartment() != null ? user.getDepartment().getName() : "General")
                .phone(user.getPhone())
                .email(user.getEmail())
                .hospitalId(user.getHospital() != null ? user.getHospital().getId() : null)
                .status(user.getStatus().name())
                .availabilityStatus(user.getStatus() == User.UserStatus.ACTIVE ? "Available" : "Unavailable")
                .todayAppointments(todayAppointments.intValue())
                .schedule(schedule)
                .build();
    }

    private Map<String, DoctorDto.ScheduleDto> createDefaultSchedule() {
        Map<String, DoctorDto.ScheduleDto> schedule = new HashMap<>();
        DoctorDto.ScheduleDto defaultSchedule = DoctorDto.ScheduleDto.builder()
                .start("09:00")
                .end("17:00")
                .build();

        schedule.put("monday", defaultSchedule);
        schedule.put("tuesday", defaultSchedule);
        schedule.put("wednesday", defaultSchedule);
        schedule.put("thursday", defaultSchedule);
        schedule.put("friday", defaultSchedule);

        return schedule;
    }

    private List<DoctorAvailabilityDto.TimeSlotDto> generateTimeSlots(List<Appointment> appointments) {
        List<DoctorAvailabilityDto.TimeSlotDto> timeSlots = new ArrayList<>();

        // Generate 30-minute slots from 9:00 AM to 5:00 PM
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);

        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            LocalTime slotEndTime = currentTime.plusMinutes(30);

            // Check if this slot is booked
            final LocalTime finalCurrentTime = currentTime;
            final LocalTime finalSlotEndTime = slotEndTime;

            Optional<Appointment> bookedAppointment = appointments.stream()
                    .filter(apt -> {
                        LocalTime aptTime = apt.getStartTime().toLocalTime();
                        return !aptTime.isBefore(finalCurrentTime) && aptTime.isBefore(finalSlotEndTime);
                    })
                    .findFirst();

            DoctorAvailabilityDto.TimeSlotDto.TimeSlotDtoBuilder slotBuilder = DoctorAvailabilityDto.TimeSlotDto.builder()
                    .startTime(currentTime.toString())
                    .endTime(slotEndTime.toString())
                    .available(bookedAppointment.isEmpty());

            bookedAppointment.ifPresent(appointment -> slotBuilder.appointmentId(appointment.getId()));

            timeSlots.add(slotBuilder.build());
            currentTime = slotEndTime;
        }

        return timeSlots;
    }

    private AvailableHospitalDto mapToAvailableHospitalDto(Hospital hospital) {
        return AvailableHospitalDto.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .address(formatHospitalAddress(hospital))
                .phone(hospital.getContact())
                .status("Active")
                .build();
    }

    private String formatHospitalAddress(Hospital hospital) {
        if (hospital.getAddress() == null) {
            return "Address not available";
        }

        StringBuilder address = new StringBuilder();
        if (hospital.getAddress().getLine1() != null) {
            address.append(hospital.getAddress().getLine1());
        }
        if (hospital.getAddress().getCity() != null) {
            if (!address.isEmpty()) address.append(", ");
            address.append(hospital.getAddress().getCity());
        }
        if (hospital.getAddress().getState() != null) {
            if (!address.isEmpty()) address.append(", ");
            address.append(hospital.getAddress().getState());
        }
        if (hospital.getAddress().getPostalCode() != null) {
            if (!address.isEmpty()) address.append(" ");
            address.append(hospital.getAddress().getPostalCode());
        }

        return !address.isEmpty() ? address.toString() : "Address not available";
    }

    private HospitalAdminListDto mapToHospitalAdminListDto(User user) {
        return HospitalAdminListDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phone(user.getPhone())
                .nationalId(user.getNationalId())
                .hospitalId(user.getHospital() != null ? user.getHospital().getId() : null)
                .hospitalName(user.getHospital() != null ? user.getHospital().getName() : null)
                .status(user.getStatus().name())
                .createdAt(user.getCreatedDt())
                .lastLoginAt(user.getLastLoginDt())
                .build();
    }

    @Override
    public com.mhms.medisynapse.dto.ReceptionistResponseDto createReceptionist(com.mhms.medisynapse.dto.CreateReceptionistDto dto) {
        log.info("Creating receptionist with email: {}", dto.getEmail());
        Map<String, String> errors = new HashMap<>();
        if (userRepository.receptionistEmailExists(dto.getEmail())) {
            errors.put("email", "Email already exists for a receptionist");
        }
        if (dto.getNationalId() != null && !dto.getNationalId().trim().isEmpty() && userRepository.existsByNationalId(dto.getNationalId())) {
            errors.put("nationalId", "National ID already in use");
        }
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty() && userRepository.existsByPhone(dto.getPhone())) {
            errors.put("phone", "Phone number already in use");
        }
        if (!hospitalRepository.existsById(dto.getHospitalId())) {
            errors.put("hospitalId", "Invalid hospital ID");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
        Hospital hospital = hospitalRepository.findById(dto.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + dto.getHospitalId()));
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setNationalId(dto.getNationalId());
        user.setRole(User.UserRole.RECEPTIONIST);
        user.setStatus(User.UserStatus.valueOf(dto.getStatus()));
        user.setHospital(hospital);
        user.setIsActive(true);
        User saved = userRepository.save(user);
        return com.mhms.medisynapse.dto.ReceptionistResponseDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .phone(saved.getPhone())
                .nationalId(saved.getNationalId())
                .hospitalId(saved.getHospital().getId())
                .hospitalName(saved.getHospital().getName())
                .status(saved.getStatus().name())
                .createdAt(saved.getCreatedDt())
                .lastUpdatedAt(saved.getLastUpdatedDt())
                .deleted(false)
                .build();
    }

    @Override
    public com.mhms.medisynapse.dto.ReceptionistResponseDto updateReceptionist(Long id, com.mhms.medisynapse.dto.UpdateReceptionistDto dto) {
        log.info("Updating receptionist with ID: {}", id);
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found with id: " + id));
        if (existing.getRole() != User.UserRole.RECEPTIONIST) {
            throw new ResourceNotFoundException("Receptionist not found with id: " + id);
        }
        Map<String, String> errors = new HashMap<>();
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty() && userRepository.receptionistEmailExistsExcludingId(dto.getEmail(), id)) {
            errors.put("email", "Email already exists");
        }
        if (dto.getNationalId() != null && !dto.getNationalId().trim().isEmpty() && userRepository.receptionistNationalIdExistsExcludingId(dto.getNationalId(), id)) {
            errors.put("nationalId", "National ID already in use");
        }
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty() && userRepository.receptionistPhoneExistsExcludingId(dto.getPhone(), id)) {
            errors.put("phone", "Phone number already in use");
        }
        if (dto.getHospitalId() != null && !hospitalRepository.existsById(dto.getHospitalId())) {
            errors.put("hospitalId", "Invalid hospital ID");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) existing.setName(dto.getName().trim());
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) existing.setEmail(dto.getEmail().trim());
        if (dto.getPhone() != null) existing.setPhone(dto.getPhone().trim().isEmpty() ? null : dto.getPhone().trim());
        if (dto.getNationalId() != null) existing.setNationalId(dto.getNationalId().trim().isEmpty() ? null : dto.getNationalId().trim());
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) existing.setStatus(User.UserStatus.valueOf(dto.getStatus()));
        if (dto.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(dto.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + dto.getHospitalId()));
            existing.setHospital(hospital);
        }
        // Reactivate if soft-deleted and status requested ACTIVE
        if (Boolean.FALSE.equals(existing.getIsActive()) && dto.getStatus() != null && dto.getStatus().equalsIgnoreCase("ACTIVE")) {
            existing.setIsActive(true);
        }
        User updated = userRepository.save(existing);
        return com.mhms.medisynapse.dto.ReceptionistResponseDto.builder()
                .id(updated.getId())
                .name(updated.getName())
                .email(updated.getEmail())
                .role(updated.getRole().name())
                .phone(updated.getPhone())
                .nationalId(updated.getNationalId())
                .hospitalId(updated.getHospital().getId())
                .hospitalName(updated.getHospital().getName())
                .status(updated.getStatus().name())
                .createdAt(updated.getCreatedDt())
                .lastUpdatedAt(updated.getLastUpdatedDt())
                .deleted(!updated.getIsActive())
                .build();
    }

    @Override
    public com.mhms.medisynapse.dto.PasswordResetResponseDto resetReceptionistPassword(Long id, com.mhms.medisynapse.dto.ResetPasswordDto resetPasswordDto) {
        log.info("Resetting password for receptionist with ID: {}", id);
        User existing = userRepository.findActiveUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found with id: " + id));
        if (existing.getRole() != User.UserRole.RECEPTIONIST) {
            throw new ResourceNotFoundException("Receptionist not found with id: " + id);
        }
        if (existing.getStatus() != User.UserStatus.ACTIVE) {
            throw new ValidationException("Password reset failed", Map.of("status", "Can only reset password for active receptionists"));
        }
        existing.setPasswordHash(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        existing.setPasswordResetDt(LocalDateTime.now());
        User updated = userRepository.save(existing);
        return PasswordResetResponseDto.builder()
                .id(updated.getId())
                .name(updated.getName())
                .email(updated.getEmail())
                .passwordResetAt(updated.getPasswordResetDt())
                .build();
    }

    @Override
    public com.mhms.medisynapse.dto.ReceptionistPagedResponseDto getReceptionists(int page, int size, String sortBy, String sortDir, Long hospitalId) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<User> pageData = userRepository.findReceptionists(hospitalId, pageable);
        List<com.mhms.medisynapse.dto.ReceptionistListDto> list = pageData.getContent().stream().map(u -> com.mhms.medisynapse.dto.ReceptionistListDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .hospitalId(u.getHospital() != null ? u.getHospital().getId() : null)
                .hospitalName(u.getHospital() != null ? u.getHospital().getName() : null)
                .status(u.getStatus().name())
                .createdAt(u.getCreatedDt())
                .lastUpdatedAt(u.getLastUpdatedDt())
                .deleted(u.getIsActive() != null && !u.getIsActive())
                .build()).toList();
        PaginationInfo pagination = PaginationInfo.builder()
                .currentPage(pageData.getNumber())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .pageSize(pageData.getSize())
                .hasNext(pageData.hasNext())
                .hasPrevious(pageData.hasPrevious())
                .build();
        return com.mhms.medisynapse.dto.ReceptionistPagedResponseDto.builder()
                .data(list)
                .pagination(pagination)
                .build();
    }

    @Override
    public com.mhms.medisynapse.dto.ReceptionistResponseDto getReceptionistById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found with id: " + id));
        if (user.getRole() != User.UserRole.RECEPTIONIST) {
            throw new ResourceNotFoundException("Receptionist not found with id: " + id);
        }
        return com.mhms.medisynapse.dto.ReceptionistResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phone(user.getPhone())
                .nationalId(user.getNationalId())
                .hospitalId(user.getHospital() != null ? user.getHospital().getId() : null)
                .hospitalName(user.getHospital() != null ? user.getHospital().getName() : null)
                .status(user.getStatus().name())
                .createdAt(user.getCreatedDt())
                .lastUpdatedAt(user.getLastUpdatedDt())
                .deleted(!user.getIsActive())
                .build();
    }

    @Override
    public void deleteReceptionist(Long id) {
        log.info("Soft deleting receptionist with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found with id: " + id));
        if (user.getRole() != User.UserRole.RECEPTIONIST) {
            throw new ResourceNotFoundException("Receptionist not found with id: " + id);
        }
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new ValidationException("Deletion failed", Map.of("receptionist", "Receptionist already deleted"));
        }
        user.setIsActive(false); // soft delete
        user.setStatus(User.UserStatus.INACTIVE); // also update status
        userRepository.save(user);
    }
}
