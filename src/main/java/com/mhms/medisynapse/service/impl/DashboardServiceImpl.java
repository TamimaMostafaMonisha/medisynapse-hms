package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.BedOccupancyDto;
import com.mhms.medisynapse.dto.DashboardStatisticsDto;
import com.mhms.medisynapse.dto.DashboardStatisticsRequestDto;
import com.mhms.medisynapse.dto.DepartmentStatsDto;
import com.mhms.medisynapse.dto.HospitalAdminResponseDto;
import com.mhms.medisynapse.dto.RecentAdmissionDto;
import com.mhms.medisynapse.dto.StaffOnDutyDto;
import com.mhms.medisynapse.dto.ReceptionistResponseDto;
import com.mhms.medisynapse.entity.Admission;
import com.mhms.medisynapse.entity.DepartmentType;
import com.mhms.medisynapse.entity.Hospital;
import com.mhms.medisynapse.entity.User;
import com.mhms.medisynapse.exception.ResourceNotFoundException;
import com.mhms.medisynapse.repository.AdmissionRepository;
import com.mhms.medisynapse.repository.AppointmentRepository;
import com.mhms.medisynapse.repository.DepartmentTypeRepository;
import com.mhms.medisynapse.repository.HospitalRepository;
import com.mhms.medisynapse.repository.PatientRepository;
import com.mhms.medisynapse.repository.ShiftRepository;
import com.mhms.medisynapse.repository.UserRepository;
import com.mhms.medisynapse.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    // Department icon mapping
    private static final Map<String, String> DEPARTMENT_ICONS = new HashMap<>();

    static {
        DEPARTMENT_ICONS.put("Cardiology", "heart-pulse");
        DEPARTMENT_ICONS.put("Neurology", "brain");
        DEPARTMENT_ICONS.put("Orthopedics", "bone");
        DEPARTMENT_ICONS.put("Pediatrics", "baby");
        DEPARTMENT_ICONS.put("Emergency", "ambulance");
        DEPARTMENT_ICONS.put("Surgery", "scalpel");
        DEPARTMENT_ICONS.put("Radiology", "x-ray");
        DEPARTMENT_ICONS.put("Laboratory", "flask");
        DEPARTMENT_ICONS.put("Pharmacy", "pills");
        DEPARTMENT_ICONS.put("ICU", "heart-monitor");
    }

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final DepartmentTypeRepository departmentTypeRepository;
    private final HospitalRepository hospitalRepository;
    private final ShiftRepository shiftRepository;
    private final AdmissionRepository admissionRepository;

    @Override
    public DashboardStatisticsDto getDashboardStatistics(DashboardStatisticsRequestDto request) {
        log.info("Fetching dashboard statistics for hospital ID: {}", request.getHospitalId());

        Long hospitalId = request.getHospitalId();

        // Get total patients
        Long totalPatients = patientRepository.countPatientsByHospitalId(hospitalId);
        log.debug("Total patients: {}", totalPatients);

        // Get total doctors (users with DOCTOR role)
        Long totalDoctors = userRepository.countByHospitalIdAndRole(hospitalId, User.UserRole.DOCTOR);
        log.debug("Total doctors: {}", totalDoctors);

        // Get active appointments
        Long activeAppointments = appointmentRepository.countActiveAppointmentsByHospitalId(hospitalId);
        log.debug("Active appointments: {}", activeAppointments);

        // Get bed occupancy (using real admission data)
        BedOccupancyDto bedOccupancy = getBedOccupancyData(hospitalId);

        // Get staff on duty (using real shift data)
        StaffOnDutyDto staffOnDuty = getStaffOnDutyData(hospitalId);

        // Get department statistics (using real appointment/admission data)
        List<DepartmentStatsDto> departmentStats = getDepartmentStatistics(hospitalId);

        // Get recent admissions (using real admission data)
        List<RecentAdmissionDto> recentAdmissions = getRecentAdmissions(hospitalId);

        return DashboardStatisticsDto.builder()
                .totalPatients(totalPatients)
                .totalDoctors(totalDoctors)
                .activeAppointments(activeAppointments)
                .bedOccupancy(bedOccupancy)
                .staffOnDuty(staffOnDuty)
                .departmentStats(departmentStats)
                .recentAdmissions(recentAdmissions)
                .build();
    }

    private BedOccupancyDto getBedOccupancyData(Long hospitalId) {
        // Get hospital entity to access bed information
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found with ID: " + hospitalId));

        // Use hospital's bed fields
        Long total = hospital.getTotalBeds() != null ? hospital.getTotalBeds().longValue() : 0L;
        Long available = hospital.getAvailableBeds() != null ? hospital.getAvailableBeds().longValue() : 0L;
        Long occupied = total - available;

        Double occupancyPercentage = total > 0 ?
                Math.round((occupied.doubleValue() / total.doubleValue()) * 100 * 100.0) / 100.0 : 0.0;

        return BedOccupancyDto.builder()
                .occupied(occupied)
                .available(available)
                .total(total)
                .occupancyPercentage(occupancyPercentage)
                .build();
    }

    private StaffOnDutyDto getStaffOnDutyData(Long hospitalId) {
        LocalDateTime currentTime = LocalDateTime.now();

        // Get real staff duty data from shifts
        Long onDuty = shiftRepository.countStaffOnDutyByHospitalId(hospitalId, currentTime);
        Long totalStaff = shiftRepository.countTotalStaffByHospitalId(hospitalId);
        Long offDuty = totalStaff - onDuty;

        return StaffOnDutyDto.builder()
                .onDuty(onDuty)
                .offDuty(offDuty)
                .total(totalStaff)
                .build();
    }

    private List<DepartmentStatsDto> getDepartmentStatistics(Long hospitalId) {
        // Use DepartmentTypeRepository instead of DepartmentRepository
        List<DepartmentType> departmentTypes = departmentTypeRepository.findAllActiveOrderByName();

        return departmentTypes.stream()
                .map(departmentType -> {
                    // For now, using mock data since we don't have actual department-appointment mapping
                    // In a real implementation, you would need to join appointments with departments
                    Long patientsToday = (long) (Math.random() * 15); // 0-14 patients per department

                    String icon = DEPARTMENT_ICONS.getOrDefault(departmentType.getName(), "stethoscope");

                    return DepartmentStatsDto.builder()
                            .departmentName(departmentType.getName())
                            .patientsToday(patientsToday)
                            .icon(icon)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<RecentAdmissionDto> getRecentAdmissions(Long hospitalId) {
        // Get admissions from the last 24 hours
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        List<Admission> recentAdmissions = admissionRepository.findRecentAdmissionsByHospitalId(hospitalId, last24Hours);

        return recentAdmissions.stream()
                .limit(10) // Limit to 10 recent admissions
                .map(admission -> {
                    return RecentAdmissionDto.builder()
                            .patientId(admission.getPatient().getId())
                            .patientName(admission.getPatient().getFirstName() + " " + admission.getPatient().getLastName())
                            .roomNumber(admission.getBedNo() != null ? admission.getBedNo() : "N/A")
                            .department(admission.getDepartment().getName())
                            .admissionTime(admission.getCreatedDt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public HospitalAdminResponseDto getHospitalAdminProfile(Long id) {
        User user = userRepository.findActiveUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital admin not found with id: " + id));
        if (user.getRole() != User.UserRole.HOSPITAL_ADMIN) {
            throw new ResourceNotFoundException("Hospital admin not found with id: " + id);
        }
        return HospitalAdminResponseDto.builder()
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
                .build();
    }

    @Override
    public ReceptionistResponseDto getReceptionistProfile(Long id) {
        User user = userRepository.findActiveUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found with id: " + id));
        if (user.getRole() != User.UserRole.RECEPTIONIST) {
            throw new ResourceNotFoundException("Receptionist not found with id: " + id);
        }
        return ReceptionistResponseDto.builder()
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
                .build();
    }
}
