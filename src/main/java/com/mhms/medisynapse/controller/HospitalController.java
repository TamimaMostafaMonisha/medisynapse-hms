package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.HospitalDto;
import com.mhms.medisynapse.dto.PagedResponse;
import com.mhms.medisynapse.service.HospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hospitals")
@RequiredArgsConstructor
@Slf4j
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping
    public ResponseEntity<PagedResponse<HospitalDto>> getAllHospitals(
            @RequestParam(value = "page", defaultValue = "0") int page,

            @RequestParam(value = "size", defaultValue = "10") int size,

            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,

            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        log.info("GET /api/v1/hospitals - page: {}, size: {}, sortBy: {}, sortDir: {}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<HospitalDto> response = hospitalService.getAllHospitals(pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<HospitalDto>> searchHospitals(
            @RequestParam(value = "name", required = false) String name,

            @RequestParam(value = "contact", required = false) String contact,

            @RequestParam(value = "page", defaultValue = "0") int page,

            @RequestParam(value = "size", defaultValue = "10") int size,

            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,

            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        log.info("GET /api/v1/hospitals/search - name: {}, contact: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                name, contact, page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<HospitalDto> response = hospitalService.getHospitalsWithFilters(name, contact, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HospitalDto> getHospitalById(
            @PathVariable Long id) {

        log.info("GET /api/v1/hospitals/{}", id);

        HospitalDto hospital = hospitalService.getHospitalById(id);
        return ResponseEntity.ok(hospital);
    }
}
