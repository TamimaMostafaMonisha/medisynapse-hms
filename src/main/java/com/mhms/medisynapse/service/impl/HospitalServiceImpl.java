package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.AddressDto;
import com.mhms.medisynapse.dto.HospitalDto;
import com.mhms.medisynapse.dto.PagedResponse;
import com.mhms.medisynapse.dto.HospitalStatsDto;
import com.mhms.medisynapse.entity.Hospital;
import com.mhms.medisynapse.exception.ResourceNotFoundException;
import com.mhms.medisynapse.repository.HospitalRepository;
import com.mhms.medisynapse.service.HospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final HospitalRepository hospitalRepository;

    @Override
    @Transactional
    public HospitalDto createHospital(HospitalDto hospitalDto) {
        log.info("Creating new hospital: {}", hospitalDto.getName());

        // Map DTO to Entity
        Hospital hospital = mapToEntity(hospitalDto);

        // Save hospital
        Hospital savedHospital = hospitalRepository.save(hospital);
        log.info("Hospital created successfully with ID: {}", savedHospital.getId());

        // Return mapped DTO
        return mapToDto(savedHospital);
    }

    @Override
    @Transactional
    public HospitalDto updateHospital(Long id, HospitalDto hospitalDto) {
        log.info("Updating hospital with ID: {} - {}", id, hospitalDto.getName());

        // Find existing active hospital
        Hospital existingHospital = hospitalRepository.findActiveHospitalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));

        // Update fields while preserving creation data
        updateHospitalFields(existingHospital, hospitalDto);

        // Save updated hospital
        Hospital updatedHospital = hospitalRepository.save(existingHospital);
        log.info("Hospital updated successfully with ID: {}", updatedHospital.getId());

        return mapToDto(updatedHospital);
    }

    @Override
    @Transactional
    public void deleteHospital(Long id) {
        log.info("Soft deleting hospital with ID: {}", id);

        // Find existing active hospital
        Hospital existingHospital = hospitalRepository.findActiveHospitalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));

        // Check for active dependencies
        checkHospitalDependencies(existingHospital);

        // Perform soft delete
        existingHospital.setIsActive(false);
        existingHospital.setLastUpdatedDt(LocalDateTime.now());
        // TODO: Set updatedBy field when authentication context is available

        hospitalRepository.save(existingHospital);
        log.info("Hospital soft deleted successfully with ID: {}", id);
    }

    @Override
    public PagedResponse<HospitalDto> getAllHospitals(Pageable pageable) {
        log.info("Fetching all hospitals with pagination - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Hospital> hospitalPage = hospitalRepository.findAllWithAddress(pageable);

        List<HospitalDto> hospitalDtos = hospitalPage.getContent()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return buildPagedResponse(hospitalPage, hospitalDtos);
    }

    @Override
    public PagedResponse<HospitalDto> getAllActiveHospitals(Pageable pageable) {
        log.info("Fetching all active hospitals with pagination - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Hospital> hospitalPage = hospitalRepository.findAllActiveWithAddress(pageable);

        List<HospitalDto> hospitalDtos = hospitalPage.getContent()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return buildPagedResponse(hospitalPage, hospitalDtos);
    }

    @Override
    public PagedResponse<HospitalDto> getHospitalsWithFilters(String name, String contact, Pageable pageable) {
        log.info("Fetching hospitals with filters - name: {}, contact: {}, page: {}, size: {}",
                name, contact, pageable.getPageNumber(), pageable.getPageSize());

        Page<Hospital> hospitalPage = hospitalRepository.findHospitalsWithFilters(name, contact, pageable);

        List<HospitalDto> hospitalDtos = hospitalPage.getContent()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return buildPagedResponse(hospitalPage, hospitalDtos);
    }

    @Override
    public HospitalDto getHospitalById(Long id) {
        log.info("Fetching hospital by ID: {}", id);

        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + id));

        return mapToDto(hospital);
    }

    @Override
    public HospitalDto getActiveHospitalById(Long id) {
        log.info("Fetching active hospital by ID: {}", id);

        Hospital hospital = hospitalRepository.findActiveHospitalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));

        return mapToDto(hospital);
    }

    @Override
    public List<HospitalDto> searchHospitalsByName(String name) {
        log.info("Searching hospitals by name: {}", name);

        List<Hospital> hospitals = hospitalRepository.searchActiveHospitalsByName(name);

        List<HospitalDto> hospitalDtos = hospitals.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        log.info("Found {} hospitals matching name: {}", hospitalDtos.size(), name);
        return hospitalDtos;
    }

    @Override
    public HospitalStatsDto getHospitalStatistics() {
        log.info("Calculating hospital statistics");

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
        BigDecimal occupancyRate = BigDecimal.ZERO;
        if (totalBeds > 0) {
            Long occupiedBeds = totalBeds - availableBeds;
            occupancyRate = BigDecimal.valueOf(occupiedBeds)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalBeds), 1, RoundingMode.HALF_UP);
        }

        // Status distribution
        Map<String, Long> statusDistribution = hospitalRepository.getStatusDistribution()
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        // Department distribution
        Map<String, Long> departmentDistribution = hospitalRepository.getDepartmentDistribution()
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        HospitalStatsDto stats = HospitalStatsDto.builder()
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

        log.info("Hospital statistics calculated - Total: {}, Active: {}, Occupancy: {}%",
                totalHospitals, activeHospitals, occupancyRate);

        return stats;
    }

    private Hospital mapToEntity(HospitalDto dto) {
        Hospital hospital = new Hospital();
        hospital.setName(dto.getName());
        hospital.setType(dto.getType());
        hospital.setAddressString(dto.getAddress());
        hospital.setPhone(dto.getPhone());
        hospital.setEmail(dto.getEmail());
        hospital.setTotalBeds(dto.getTotalBeds());
        hospital.setAvailableBeds(dto.getAvailableBeds());
        hospital.setTotalDepartments(dto.getTotalDepartments());
        hospital.setTotalStaff(dto.getTotalStaff());
        hospital.setEstablished(dto.getEstablished());
        hospital.setAccreditation(dto.getAccreditation());
        hospital.setStatus(dto.getStatus());
        hospital.setAdminId(dto.getAdminId());
        hospital.setContact(dto.getContact()); // Backward compatibility
        hospital.setIsActive(true);
        hospital.setCreatedDt(LocalDateTime.now());
        hospital.setLastUpdatedDt(LocalDateTime.now());

        return hospital;
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

    private void checkHospitalDependencies(Hospital hospital) {
        log.debug("Checking dependencies for hospital ID: {}", hospital.getId());

        // Check for active users
        if (hospital.getUsers() != null && !hospital.getUsers().isEmpty()) {
            long activeUsers = hospital.getUsers().stream()
                    .filter(user -> user.getStatus() != null &&
                            !"INACTIVE".equals(user.getStatus().toString()))
                    .count();
            if (activeUsers > 0) {
                throw new IllegalStateException("Cannot delete hospital. It has " + activeUsers + " active users.");
            }
        }

        // Check for active patient hospitals
        if (hospital.getPatientHospitals() != null && !hospital.getPatientHospitals().isEmpty()) {
            long activePatientHospitals = hospital.getPatientHospitals().stream()
                    .filter(ph -> ph.getStatus() != null &&
                            !"INACTIVE".equals(ph.getStatus().toString()))
                    .count();
            if (activePatientHospitals > 0) {
                throw new IllegalStateException("Cannot delete hospital. It has " + activePatientHospitals + " active patient associations.");
            }
        }

        // Check for active appointments
        if (hospital.getAppointments() != null && !hospital.getAppointments().isEmpty()) {
            long activeAppointments = hospital.getAppointments().stream()
                    .filter(appointment -> appointment.getStatus() != null &&
                            !"CANCELLED".equals(appointment.getStatus().toString()) &&
                            !"COMPLETED".equals(appointment.getStatus().toString()))
                    .count();
            if (activeAppointments > 0) {
                throw new IllegalStateException("Cannot delete hospital. It has " + activeAppointments + " active appointments.");
            }
        }

        log.debug("No active dependencies found for hospital ID: {}", hospital.getId());
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
