package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.BedManagementResponseDto;
import com.mhms.medisynapse.dto.BedSummaryDto;
import com.mhms.medisynapse.dto.DepartmentRoomsDto;
import com.mhms.medisynapse.dto.RoomDto;
import com.mhms.medisynapse.entity.Admission;
import com.mhms.medisynapse.entity.Department;
import com.mhms.medisynapse.entity.Hospital;
import com.mhms.medisynapse.repository.AdmissionRepository;
import com.mhms.medisynapse.repository.DepartmentRepository;
import com.mhms.medisynapse.repository.HospitalRepository;
import com.mhms.medisynapse.service.BedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BedServiceImpl implements BedService {

    private final HospitalRepository hospitalRepository;
    private final AdmissionRepository admissionRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public BedManagementResponseDto getBedOccupancyStatus(Long hospitalId) {
        log.info("Fetching bed occupancy status for hospital ID: {}", hospitalId);

        // Get hospital information
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found with ID: " + hospitalId));

        // Get bed summary
        BedSummaryDto bedSummary = getBedSummary(hospital);

        // Get rooms by department
        List<DepartmentRoomsDto> roomsByDepartment = getRoomsByDepartment(hospitalId);

        return BedManagementResponseDto.builder()
                .bedSummary(bedSummary)
                .roomsByDepartment(roomsByDepartment)
                .build();
    }

    private BedSummaryDto getBedSummary(Hospital hospital) {
        Long totalBeds = hospital.getTotalBeds() != null ? hospital.getTotalBeds().longValue() : 0L;
        Long availableBeds = hospital.getAvailableBeds() != null ? hospital.getAvailableBeds().longValue() : 0L;
        Long occupiedBeds = totalBeds - availableBeds;
        Long maintenanceBeds = 0L; // For now, assuming no beds in maintenance

        log.debug("Bed summary - Total: {}, Occupied: {}, Available: {}, Maintenance: {}",
                totalBeds, occupiedBeds, availableBeds, maintenanceBeds);

        return BedSummaryDto.builder()
                .total(totalBeds)
                .occupied(occupiedBeds)
                .available(availableBeds)
                .maintenance(maintenanceBeds)
                .build();
    }

    private List<DepartmentRoomsDto> getRoomsByDepartment(Long hospitalId) {
        // Get all current admissions with bed assignments
        List<Admission> currentAdmissions = admissionRepository.findCurrentAdmissionsByHospitalIdWithBeds(hospitalId);

        // Get all departments for the hospital
        List<Department> departments = departmentRepository.findByHospitalId(hospitalId);

        // Group admissions by department
        Map<Long, List<Admission>> admissionsByDepartment = currentAdmissions.stream()
                .collect(Collectors.groupingBy(admission -> admission.getDepartment().getId()));

        List<DepartmentRoomsDto> result = new ArrayList<>();

        for (Department department : departments) {
            List<Admission> departmentAdmissions = admissionsByDepartment.getOrDefault(department.getId(), new ArrayList<>());

            // Generate rooms for this department based on current admissions
            List<RoomDto> rooms = generateRoomsFromAdmissions(departmentAdmissions);

            DepartmentRoomsDto departmentRooms = DepartmentRoomsDto.builder()
                    .department(department.getName())
                    .rooms(rooms)
                    .build();

            result.add(departmentRooms);
        }

        return result;
    }

    private List<RoomDto> generateRoomsFromAdmissions(List<Admission> admissions) {
        // Group admissions by room (extract room from bed number)
        Map<String, List<Admission>> admissionsByRoom = new HashMap<>();

        for (Admission admission : admissions) {
            String bedNo = admission.getBedNo();
            String roomNumber = extractRoomFromBedNumber(bedNo);

            admissionsByRoom.computeIfAbsent(roomNumber, k -> new ArrayList<>()).add(admission);
        }

        List<RoomDto> rooms = new ArrayList<>();

        for (Map.Entry<String, List<Admission>> entry : admissionsByRoom.entrySet()) {
            String roomNumber = entry.getKey();
            List<Admission> roomAdmissions = entry.getValue();

            // Calculate bed count and occupied beds
            int occupiedBeds = roomAdmissions.size();
            // Assume rooms have 2-4 beds typically, but at least the number of occupied beds
            int totalBedsInRoom = Math.max(occupiedBeds, 2);

            // Get patient IDs for this room
            List<Long> patientIds = roomAdmissions.stream()
                    .map(admission -> admission.getPatient().getId())
                    .collect(Collectors.toList());

            RoomDto room = RoomDto.builder()
                    .roomNumber(roomNumber)
                    .bedCount(totalBedsInRoom)
                    .occupiedBeds(occupiedBeds)
                    .patientIds(patientIds)
                    .build();

            rooms.add(room);
        }

        return rooms;
    }

    private String extractRoomFromBedNumber(String bedNo) {
        if (bedNo == null || bedNo.trim().isEmpty()) {
            return "Unknown";
        }

        // Extract room number from bed number
        // Assuming bed numbers are in format like "A-301-1", "A-301-2" (room A-301, bed 1, 2)
        // or "301-A", "301-B" (room 301, bed A, B)
        // If no pattern matches, use the bed number as room number

        String trimmedBedNo = bedNo.trim();

        // Pattern: A-301-1 -> A-301
        if (trimmedBedNo.matches("^[A-Z]-\\d+-\\d+$")) {
            int lastDashIndex = trimmedBedNo.lastIndexOf('-');
            return trimmedBedNo.substring(0, lastDashIndex);
        }

        // Pattern: 301-A -> 301
        if (trimmedBedNo.matches("^\\d+-[A-Z]$")) {
            int dashIndex = trimmedBedNo.indexOf('-');
            return trimmedBedNo.substring(0, dashIndex);
        }

        // Pattern: 301A -> 301
        if (trimmedBedNo.matches("^\\d+[A-Z]$")) {
            return trimmedBedNo.replaceAll("[A-Z]$", "");
        }

        // Default: use the bed number as room number
        return trimmedBedNo;
    }
}
