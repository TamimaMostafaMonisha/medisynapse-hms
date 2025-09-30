package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.AvailableHospitalDto;
import com.mhms.medisynapse.dto.CreateHospitalAdminDto;
import com.mhms.medisynapse.dto.HospitalAdminListDto;
import com.mhms.medisynapse.dto.HospitalAdminPagedResponseDto;
import com.mhms.medisynapse.dto.HospitalAdminResponseDto;
import com.mhms.medisynapse.dto.PaginationInfo;
import com.mhms.medisynapse.dto.PasswordResetResponseDto;
import com.mhms.medisynapse.dto.ResetPasswordDto;
import com.mhms.medisynapse.dto.UpdateHospitalAdminDto;
import com.mhms.medisynapse.entity.Hospital;
import com.mhms.medisynapse.entity.User;
import com.mhms.medisynapse.exception.ResourceNotFoundException;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;

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
        log.info("Fetching available hospitals without assigned admins");

        // Get IDs of hospitals that don't have hospital admins
        List<Long> availableHospitalIds = userRepository.findAvailableHospitalIds();

        if (availableHospitalIds.isEmpty()) {
            log.info("No available hospitals found");
            return List.of();
        }

        // Get hospital details ordered by name
        List<Hospital> hospitals = hospitalRepository.findAvailableHospitalsByIds(availableHospitalIds);

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
            if (address.length() > 0) address.append(", ");
            address.append(hospital.getAddress().getCity());
        }
        if (hospital.getAddress().getState() != null) {
            if (address.length() > 0) address.append(", ");
            address.append(hospital.getAddress().getState());
        }
        if (hospital.getAddress().getPostalCode() != null) {
            if (address.length() > 0) address.append(" ");
            address.append(hospital.getAddress().getPostalCode());
        }

        return address.length() > 0 ? address.toString() : "Address not available";
    }

    private HospitalAdminListDto mapToHospitalAdminListDto(User user) {
        return HospitalAdminListDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phone(user.getPhone())
                .hospitalId(user.getHospital() != null ? user.getHospital().getId() : null)
                .hospitalName(user.getHospital() != null ? user.getHospital().getName() : null)
                .status(user.getStatus().name())
                .createdAt(user.getCreatedDt())
                .lastLoginAt(user.getLastLoginDt())
                .build();
    }

    public static class ValidationException extends RuntimeException {
        private final Map<String, String> errors;

        public ValidationException(String message, Map<String, String> errors) {
            super(message);
            this.errors = errors;
        }

        public Map<String, String> getErrors() {
            return errors;
        }
    }
}
