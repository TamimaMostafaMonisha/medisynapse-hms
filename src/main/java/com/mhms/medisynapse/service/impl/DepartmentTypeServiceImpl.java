package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.DepartmentTypeDto;
import com.mhms.medisynapse.entity.DepartmentType;
import com.mhms.medisynapse.repository.DepartmentTypeRepository;
import com.mhms.medisynapse.service.DepartmentTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DepartmentTypeServiceImpl implements DepartmentTypeService {

    private final DepartmentTypeRepository departmentTypeRepository;

    @Override
    public List<DepartmentTypeDto> getAllAvailableDepartmentTypes() {
        log.info("Fetching all available department types");

        List<DepartmentType> departmentTypes = departmentTypeRepository.findAllActiveOrderByName();

        List<DepartmentTypeDto> departmentTypeDtos = departmentTypes.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        log.info("Found {} available department types", departmentTypeDtos.size());
        return departmentTypeDtos;
    }

    private DepartmentTypeDto mapToDto(DepartmentType departmentType) {
        return DepartmentTypeDto.builder()
                .id(departmentType.getId())
                .name(departmentType.getName())
                .code(departmentType.getCode())
                .description(departmentType.getDescription())
                .build();
    }
}
