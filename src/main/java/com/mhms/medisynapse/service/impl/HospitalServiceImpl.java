package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.AddressDto;
import com.mhms.medisynapse.dto.HospitalDto;
import com.mhms.medisynapse.dto.PagedResponse;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;

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
                .contact(hospital.getContact())
                .address(addressDto)
                .createdDt(hospital.getCreatedDt())
                .lastUpdatedDt(hospital.getLastUpdatedDt())
                .build();
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
