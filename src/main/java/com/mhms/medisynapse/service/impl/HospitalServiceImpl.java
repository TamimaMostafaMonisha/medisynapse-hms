package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.constants.ErrorMessages;
import com.mhms.medisynapse.dto.AddressDto;
import com.mhms.medisynapse.dto.HospitalDto;
import com.mhms.medisynapse.dto.HospitalStatsDto;
import com.mhms.medisynapse.dto.PagedResponse;
import com.mhms.medisynapse.entity.Hospital;
import com.mhms.medisynapse.exception.BusinessLogicException;
import com.mhms.medisynapse.exception.InvalidDataException;
import com.mhms.medisynapse.exception.ResourceNotFoundException;
import com.mhms.medisynapse.repository.HospitalRepository;
import com.mhms.medisynapse.service.HospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HospitalServiceImpl implements HospitalService {

    // Constants for validation and business rules
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MIN_BEDS = 1;
    private final HospitalRepository hospitalRepository;

    @Override
    @Transactional
    public HospitalDto createHospital(HospitalDto hospitalDto) {
        log.info("Creating new hospital: {}", hospitalDto.getName());

        try {
            // Validate input
            validateHospitalDto(hospitalDto, false);

            // Check business rules
            validateBusinessRulesForCreate(hospitalDto);

            Hospital hospital = Hospital.builder()
                    .name(hospitalDto.getName())
                    .type(hospitalDto.getType())
                    .addressString(hospitalDto.getAddress())
                    .phone(hospitalDto.getPhone())
                    .email(hospitalDto.getEmail())
                    .totalBeds(hospitalDto.getTotalBeds())
                    .availableBeds(hospitalDto.getAvailableBeds())
                    .totalDepartments(hospitalDto.getTotalDepartments())
                    .totalStaff(hospitalDto.getTotalStaff())
                    .established(hospitalDto.getEstablished())
                    .accreditation(hospitalDto.getAccreditation())
                    .status(hospitalDto.getStatus())
                    .adminId(hospitalDto.getAdminId())
                    .contact(hospitalDto.getContact())
                    .isActive(true)
                    .createdDt(LocalDateTime.now())
                    .lastUpdatedDt(LocalDateTime.now())
                    .build();
            ;

            // Save hospital
            Hospital savedHospital = hospitalRepository.save(hospital);

            log.info("Hospital created successfully with ID: {} and name: {}",
                    savedHospital.getId(), savedHospital.getName());

            // Return mapped DTO
            return mapToDto(savedHospital);

        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation while creating hospital: {}", hospitalDto.getName(), ex);
            throw new BusinessLogicException("Hospital creation failed due to data constraints. " +
                    "Please check if hospital name or email already exists.");
        } catch (Exception ex) {
            log.error("Unexpected error while creating hospital: {}", hospitalDto.getName(), ex);
            throw new BusinessLogicException("Failed to create hospital: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public HospitalDto updateHospital(Long id, HospitalDto hospitalDto) {
        log.info("Updating hospital with ID: {} - name: {}", id, hospitalDto.getName());

        try {
            // Validate input
            validateId(id);
            validateHospitalDto(hospitalDto, true);

            // Find existing active hospital
            Hospital existingHospital = findActiveHospitalById(id);

            // Check business rules for update
            validateBusinessRulesForUpdate(id, hospitalDto);

            // Update fields while preserving creation data
            updateHospitalFields(existingHospital, hospitalDto);

            // Save updated hospital
            Hospital updatedHospital = hospitalRepository.save(existingHospital);

            log.info("Hospital updated successfully with ID: {} - name: {}",
                    updatedHospital.getId(), updatedHospital.getName());

            return mapToDto(updatedHospital);

        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation while updating hospital ID: {}", id, ex);
            throw new BusinessLogicException("Hospital update failed due to data constraints. " +
                    "Please check if hospital name or email already exists.");
        } catch (ResourceNotFoundException | InvalidDataException | BusinessLogicException ex) {
            // Re-throw known exceptions
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while updating hospital ID: {}", id, ex);
            throw new BusinessLogicException("Failed to update hospital: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteHospital(Long id) {
        log.info("Soft deleting hospital with ID: {}", id);

        try {
            // Validate input
            validateId(id);

            // Find existing active hospital
            Hospital existingHospital = findActiveHospitalById(id);

            // Check for active dependencies
            validateHospitalCanBeDeleted(existingHospital);

            // Perform soft delete
            performSoftDelete(existingHospital);

            log.info("Hospital soft deleted successfully with ID: {} - name: {}",
                    id, existingHospital.getName());

        } catch (ResourceNotFoundException | BusinessLogicException ex) {
            // Re-throw known exceptions
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while deleting hospital ID: {}", id, ex);
            throw new BusinessLogicException("Failed to delete hospital: " + ex.getMessage());
        }
    }

    @Override
    public PagedResponse<HospitalDto> getAllHospitals(Pageable pageable) {
        log.info("Fetching all hospitals - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<Hospital> hospitalPage = hospitalRepository.findAllWithAddress(pageable);
            List<HospitalDto> hospitalDtos = mapToDtoList(hospitalPage.getContent());

            PagedResponse<HospitalDto> response = buildPagedResponse(hospitalPage, hospitalDtos);

            log.info("Retrieved {} hospitals out of {} total",
                    hospitalDtos.size(), hospitalPage.getTotalElements());

            return response;

        } catch (Exception ex) {
            log.error("Error fetching all hospitals with pagination", ex);
            throw new BusinessLogicException("Failed to retrieve hospitals: " + ex.getMessage());
        }
    }

    @Override
    public PagedResponse<HospitalDto> getAllActiveHospitals(Pageable pageable) {
        log.info("Fetching active hospitals - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<Hospital> hospitalPage = hospitalRepository.findAllActiveWithAddress(pageable);
            List<HospitalDto> hospitalDtos = mapToDtoList(hospitalPage.getContent());

            PagedResponse<HospitalDto> response = buildPagedResponse(hospitalPage, hospitalDtos);

            log.info("Get {} active hospitals info out of {} total hospitals",
                    hospitalDtos.size(), hospitalPage.getTotalElements());

            return response;

        } catch (Exception ex) {
            log.error("Error fetching active hospitals with pagination", ex);
            throw new BusinessLogicException("Failed to retrieve active hospitals: " + ex.getMessage());
        }
    }

    @Override
    public PagedResponse<HospitalDto> getHospitalsWithFilters(String name, String contact, Pageable pageable) {
        log.info("Fetching hospitals with filters - name: '{}', contact: '{}', page: {}, size: {}",
                name, contact, pageable.getPageNumber(), pageable.getPageSize());

        try {
            // Sanitize filter inputs
            String sanitizedName = StringUtils.hasText(name) ? name.trim() : null;
            String sanitizedContact = StringUtils.hasText(contact) ? contact.trim() : null;

            Page<Hospital> hospitalPage = hospitalRepository.findHospitalsWithFilters(
                    sanitizedName, sanitizedContact, pageable);
            List<HospitalDto> hospitalDtos = mapToDtoList(hospitalPage.getContent());

            PagedResponse<HospitalDto> response = buildPagedResponse(hospitalPage, hospitalDtos);

            log.info("Found {} hospitals matching filters out of {} total",
                    hospitalDtos.size(), hospitalPage.getTotalElements());

            return response;

        } catch (Exception ex) {
            log.error("Error fetching hospitals with filters - name: '{}', contact: '{}'", name, contact, ex);
            throw new BusinessLogicException("Failed to retrieve hospitals with filters: " + ex.getMessage());
        }
    }

    @Override
    public HospitalDto getHospitalById(Long id) {
        log.info("Fetching hospital by ID: {}", id);

        try {
            validateId(id);

            Hospital hospital = hospitalRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format(ErrorMessages.HOSPITAL_NOT_FOUND, id)));

            HospitalDto hospitalDto = mapToDto(hospital);

            log.info("Retrieved hospital: {} (Active: {})", hospital.getName(), hospital.getIsActive());

            return hospitalDto;

        } catch (ResourceNotFoundException ex) {
            // Re-throw known exceptions
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error fetching hospital by ID: {}", id, ex);
            throw new BusinessLogicException("Failed to retrieve hospital: " + ex.getMessage());
        }
    }

    @Override
    public HospitalDto getActiveHospitalById(Long id) {
        log.info("Fetching active hospital by ID: {}", id);

        try {
            validateId(id);

            Hospital hospital = findActiveHospitalById(id);
            HospitalDto hospitalDto = mapToDto(hospital);

            log.info("Retrieved active hospital: {}", hospital.getName());

            return hospitalDto;

        } catch (ResourceNotFoundException ex) {
            // Re-throw known exceptions
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error fetching active hospital by ID: {}", id, ex);
            throw new BusinessLogicException("Failed to retrieve active hospital: " + ex.getMessage());
        }
    }

    @Override
    public List<HospitalDto> searchHospitalsByName(String name) {
        log.info("Searching hospitals by name: '{}'", name);

        try {
            // Validate and sanitize input
            if (!StringUtils.hasText(name)) {
                throw new InvalidDataException("Search name cannot be empty");
            }

            String sanitizedName = name.trim();
            if (sanitizedName.length() > MAX_NAME_LENGTH) {
                throw new InvalidDataException(
                        String.format("Search name cannot exceed %d characters", MAX_NAME_LENGTH));
            }

            List<Hospital> hospitals = hospitalRepository.searchActiveHospitalsByName(sanitizedName);
            List<HospitalDto> hospitalDtos = mapToDtoList(hospitals);

            log.info("Found {} hospitals matching name: '{}'", hospitalDtos.size(), sanitizedName);

            return hospitalDtos;

        } catch (InvalidDataException ex) {
            // Re-throw known exceptions
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error searching hospitals by name: '{}'", name, ex);
            throw new BusinessLogicException("Failed to search hospitals: " + ex.getMessage());
        }
    }

    @Override
    public HospitalStatsDto getHospitalStatistics() {
        log.info("Calculating hospital statistics");

        try {
            // Calculate all statistics
            HospitalStatsDto stats = calculateHospitalStatistics();

            log.info("Hospital statistics calculated - Total: {}, Active: {}, Occupancy: {}%",
                    stats.getTotalHospitals(), stats.getActiveHospitals(), stats.getOccupancyRate());

            return stats;

        } catch (Exception ex) {
            log.error("Error calculating hospital statistics", ex);
            throw new BusinessLogicException("Failed to calculate hospital statistics: " + ex.getMessage());
        }
    }

    // Private helper methods

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException(String.format(ErrorMessages.INVALID_ID_FORMAT, id));
        }
    }

    private void validateHospitalDto(HospitalDto hospitalDto, boolean isUpdate) {
        if (hospitalDto == null) {
            throw new InvalidDataException(ErrorMessages.FIELD_CANNOT_BE_NULL.replace("%s", "Hospital data"));
        }

        // Validate required fields
        if (!StringUtils.hasText(hospitalDto.getName())) {
            throw new InvalidDataException(String.format(ErrorMessages.FIELD_CANNOT_BE_EMPTY, "Hospital name"));
        }

        if (hospitalDto.getName().length() > MAX_NAME_LENGTH) {
            throw new InvalidDataException(
                    String.format(ErrorMessages.FIELD_TOO_LONG, "Hospital name", MAX_NAME_LENGTH));
        }

        if (!StringUtils.hasText(hospitalDto.getEmail())) {
            throw new InvalidDataException(String.format(ErrorMessages.FIELD_CANNOT_BE_EMPTY, "Hospital email"));
        }

        // Validate bed counts
        validateBedCounts(hospitalDto);

        // Additional validations for create
        if (!isUpdate) {
            validateRequiredFieldsForCreate(hospitalDto);
        }
    }

    private void validateBedCounts(HospitalDto hospitalDto) {
        if (hospitalDto.getTotalBeds() == null || hospitalDto.getTotalBeds() < MIN_BEDS) {
            throw new InvalidDataException(
                    String.format(ErrorMessages.INVALID_BED_COUNT, MIN_BEDS));
        }

        if (hospitalDto.getAvailableBeds() == null || hospitalDto.getAvailableBeds() < 0) {
            throw new InvalidDataException(ErrorMessages.VALUE_OUT_OF_RANGE.replace("%s", "Available beds").replace("%d", "0").replace("%d", "total beds"));
        }

        if (hospitalDto.getAvailableBeds() > hospitalDto.getTotalBeds()) {
            throw new InvalidDataException(
                    String.format(ErrorMessages.AVAILABLE_BEDS_EXCEED_TOTAL,
                            hospitalDto.getAvailableBeds(), hospitalDto.getTotalBeds()));
        }
    }

    private void validateRequiredFieldsForCreate(HospitalDto hospitalDto) {
        if (!StringUtils.hasText(hospitalDto.getType())) {
            throw new InvalidDataException(String.format(ErrorMessages.FIELD_CANNOT_BE_EMPTY, "Hospital type"));
        }

        if (!StringUtils.hasText(hospitalDto.getAddress())) {
            throw new InvalidDataException(String.format(ErrorMessages.FIELD_CANNOT_BE_EMPTY, "Hospital address"));
        }

        if (!StringUtils.hasText(hospitalDto.getPhone())) {
            throw new InvalidDataException(String.format(ErrorMessages.FIELD_CANNOT_BE_EMPTY, "Hospital phone"));
        }
    }

    private void validateBusinessRulesForCreate(HospitalDto hospitalDto) {
        // Check for duplicate name
        if (hospitalRepository.existsByNameIgnoreCase(hospitalDto.getName().trim())) {
            throw new BusinessLogicException(
                    String.format(ErrorMessages.HOSPITAL_ALREADY_EXISTS, hospitalDto.getName()));
        }

        // Check for duplicate email
        if (hospitalRepository.existsByEmailIgnoreCase(hospitalDto.getEmail().trim())) {
            throw new BusinessLogicException(
                    String.format(ErrorMessages.HOSPITAL_EMAIL_ALREADY_EXISTS, hospitalDto.getEmail()));
        }
    }

    private void validateBusinessRulesForUpdate(Long id, HospitalDto hospitalDto) {
        // Check for duplicate name (excluding current hospital)
        if (hospitalRepository.existsByNameIgnoreCaseAndIdNot(hospitalDto.getName().trim(), id)) {
            throw new BusinessLogicException(
                    String.format(ErrorMessages.HOSPITAL_ALREADY_EXISTS, hospitalDto.getName()));
        }

        // Check for duplicate email (excluding current hospital)
        if (hospitalRepository.existsByEmailIgnoreCaseAndIdNot(hospitalDto.getEmail().trim(), id)) {
            throw new BusinessLogicException(
                    String.format(ErrorMessages.HOSPITAL_EMAIL_ALREADY_EXISTS, hospitalDto.getEmail()));
        }
    }

    private Hospital findActiveHospitalById(Long id) {
        return hospitalRepository.findActiveHospitalById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.HOSPITAL_NOT_FOUND, id)));
    }

    private void validateHospitalCanBeDeleted(Hospital hospital) {
        log.debug("Checking dependencies for hospital ID: {}", hospital.getId());

        try {
            // Check for active users
            validateNoActiveUsers(hospital);

            // Check for active patient hospitals
            validateNoActivePatientAssociations(hospital);

            // Check for active appointments
            validateNoActiveAppointments(hospital);

            log.debug("No active dependencies found for hospital ID: {}", hospital.getId());

        } catch (Exception ex) {
            log.warn("Dependency check failed for hospital ID: {} - {}", hospital.getId(), ex.getMessage());
            throw new BusinessLogicException("Cannot delete hospital: " + ex.getMessage());
        }
    }

    private void validateNoActiveUsers(Hospital hospital) {
        if (hospital.getUsers() != null && !hospital.getUsers().isEmpty()) {
            long activeUsers = hospital.getUsers().stream()
                    .filter(user -> user.getStatus() != null &&
                            !"INACTIVE".equals(user.getStatus().toString()))
                    .count();
            if (activeUsers > 0) {
                throw new BusinessLogicException(
                        String.format(ErrorMessages.HOSPITAL_HAS_ACTIVE_USERS, activeUsers));
            }
        }
    }

    private void validateNoActivePatientAssociations(Hospital hospital) {
        if (hospital.getPatientHospitals() != null && !hospital.getPatientHospitals().isEmpty()) {
            long activePatientHospitals = hospital.getPatientHospitals().stream()
                    .filter(ph -> ph.getStatus() != null &&
                            !"INACTIVE".equals(ph.getStatus().toString()))
                    .count();
            if (activePatientHospitals > 0) {
                throw new BusinessLogicException(
                        String.format(ErrorMessages.HOSPITAL_HAS_ACTIVE_PATIENTS, activePatientHospitals));
            }
        }
    }

    private void validateNoActiveAppointments(Hospital hospital) {
        if (hospital.getAppointments() != null && !hospital.getAppointments().isEmpty()) {
            long activeAppointments = hospital.getAppointments().stream()
                    .filter(appointment -> appointment.getStatus() != null &&
                            !"CANCELLED".equals(appointment.getStatus().toString()) &&
                            !"COMPLETED".equals(appointment.getStatus().toString()))
                    .count();
            if (activeAppointments > 0) {
                throw new BusinessLogicException(
                        String.format(ErrorMessages.HOSPITAL_HAS_ACTIVE_APPOINTMENTS, activeAppointments));
            }
        }
    }

    private void performSoftDelete(Hospital hospital) {
        hospital.setIsActive(false);
        hospital.setLastUpdatedDt(LocalDateTime.now());
        // TODO: Set updatedBy field when authentication context is available
        hospitalRepository.save(hospital);
    }

    private List<HospitalDto> mapToDtoList(List<Hospital> hospitals) {
        return hospitals.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private HospitalStatsDto calculateHospitalStatistics() {
        // Basic counts
        Long totalHospitals = hospitalRepository.countTotalHospitals();
        Long activeHospitals = hospitalRepository.countActiveHospitals();
        Long inactiveHospitals = hospitalRepository.countInactiveHospitals();

        // Bed statistics
        Long totalBeds = hospitalRepository.sumTotalBeds();
        Long availableBeds = hospitalRepository.sumAvailableBeds();

        // Staff statistics
        Long totalStaff = hospitalRepository.sumTotalStaff();
        Integer averageBedsPerHospital = hospitalRepository.averageBedsPerHospital();

        // Calculate occupancy rate
        BigDecimal occupancyRate = calculateOccupancyRate(totalBeds, availableBeds);

        // Distribution data
        Map<String, Long> statusDistribution = getStatusDistribution();
        Map<String, Long> departmentDistribution = getDepartmentDistribution();

        return HospitalStatsDto.builder()
                .totalHospitals(totalHospitals)
                .activeHospitals(activeHospitals)
                .inactiveHospitals(inactiveHospitals)
                .totalBeds(totalBeds)
                .availableBeds(availableBeds)
                .occupancyRate(occupancyRate)
                .totalStaff(totalStaff)
                .averageBedsPerHospital(averageBedsPerHospital)
                .departmentDistribution(departmentDistribution)
                .statusDistribution(statusDistribution)
                .build();
    }

    private BigDecimal calculateOccupancyRate(Long totalBeds, Long availableBeds) {
        if (totalBeds == null || totalBeds <= 0 || availableBeds == null) {
            return BigDecimal.ZERO;
        }

        long occupiedBeds = totalBeds - availableBeds;
        return BigDecimal.valueOf(occupiedBeds)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalBeds), 1, RoundingMode.HALF_UP);
    }

    private Map<String, Long> getStatusDistribution() {
        try {
            return hospitalRepository.getStatusDistribution()
                    .stream()
                    .collect(Collectors.toMap(
                            row -> (String) row[0],
                            row -> (Long) row[1]
                    ));
        } catch (Exception ex) {
            log.warn("Error getting status distribution, returning empty map", ex);
            return Map.of();
        }
    }

    private Map<String, Long> getDepartmentDistribution() {
        try {
            return hospitalRepository.getDepartmentDistribution()
                    .stream()
                    .collect(Collectors.toMap(
                            row -> (String) row[0],
                            row -> (Long) row[1]
                    ));
        } catch (Exception ex) {
            log.warn("Error getting department distribution, returning empty map", ex);
            return Map.of();
        }
    }

    private HospitalDto mapToDto(Hospital hospital) {
        AddressDto addressDto = null;
        if (hospital.getAddress() != null) {
            addressDto = AddressDto.builder()
                    .id(hospital.getAddress().getId())
                    .line1(hospital.getAddress().getLine1())
                    .line2(hospital.getAddress().getLine2())
                    .city(hospital.getAddress().getCity())
                    .state(hospital.getAddress().getState())
                    .postalCode(hospital.getAddress().getPostalCode())
                    .country(hospital.getAddress().getCountry())
                    .type(hospital.getAddress().getType() != null ? hospital.getAddress().getType().toString() : null)
                    .build();
        }

        return HospitalDto.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .type(hospital.getType())
                .address(hospital.getAddressString())
                .phone(hospital.getPhone())
                .email(hospital.getEmail())
                .totalBeds(hospital.getTotalBeds())
                .availableBeds(hospital.getAvailableBeds())
                .totalDepartments(hospital.getTotalDepartments())
                .totalStaff(hospital.getTotalStaff())
                .established(hospital.getEstablished())
                .accreditation(hospital.getAccreditation())
                .status(hospital.getStatus())
                .adminId(hospital.getAdminId())
                .createdAt(hospital.getCreatedDt())
                .updatedAt(hospital.getLastUpdatedDt())
                // Backward compatibility fields
                .contact(hospital.getContact())
                .addressDto(addressDto)
                .createdDt(hospital.getCreatedDt())
                .lastUpdatedDt(hospital.getLastUpdatedDt())
                .build();
    }

    private void updateHospitalFields(Hospital existingHospital, HospitalDto hospitalDto) {
        existingHospital.setName(hospitalDto.getName());
        existingHospital.setType(hospitalDto.getType());
        existingHospital.setAddressString(hospitalDto.getAddress());
        existingHospital.setPhone(hospitalDto.getPhone());
        existingHospital.setEmail(hospitalDto.getEmail());
        existingHospital.setTotalBeds(hospitalDto.getTotalBeds());
        existingHospital.setAvailableBeds(hospitalDto.getAvailableBeds());
        existingHospital.setTotalDepartments(hospitalDto.getTotalDepartments());
        existingHospital.setTotalStaff(hospitalDto.getTotalStaff());
        existingHospital.setEstablished(hospitalDto.getEstablished());
        existingHospital.setAccreditation(hospitalDto.getAccreditation());
        existingHospital.setStatus(hospitalDto.getStatus());
        existingHospital.setAdminId(hospitalDto.getAdminId());
        existingHospital.setContact(hospitalDto.getContact()); // Backward compatibility
        existingHospital.setLastUpdatedDt(LocalDateTime.now());
    }

    private PagedResponse<HospitalDto> buildPagedResponse(Page<Hospital> hospitalPage, List<HospitalDto> hospitalDtos) {
        return PagedResponse.<HospitalDto>builder()
                .content(hospitalDtos)
                .pageNumber(hospitalPage.getNumber())
                .pageSize(hospitalPage.getSize())
                .totalElements(hospitalPage.getTotalElements())
                .totalPages(hospitalPage.getTotalPages())
                .first(hospitalPage.isFirst())
                .last(hospitalPage.isLast())
                .hasNext(hospitalPage.hasNext())
                .hasPrevious(hospitalPage.hasPrevious())
                .build();
    }
}
