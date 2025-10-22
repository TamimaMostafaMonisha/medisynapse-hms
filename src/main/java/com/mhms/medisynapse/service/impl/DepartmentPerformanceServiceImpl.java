package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.DepartmentPerformanceDto;
import com.mhms.medisynapse.dto.DepartmentPerformanceResponseDto;
import com.mhms.medisynapse.entity.Appointment;
import com.mhms.medisynapse.entity.Department;
import com.mhms.medisynapse.repository.AppointmentRepository;
import com.mhms.medisynapse.repository.DepartmentRepository;
import com.mhms.medisynapse.repository.UserRepository;
import com.mhms.medisynapse.service.DepartmentPerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DepartmentPerformanceServiceImpl implements DepartmentPerformanceService {

    private final DepartmentRepository departmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    public DepartmentPerformanceResponseDto getDepartmentPerformance(Long hospitalId) {
        log.info("Fetching department performance metrics for hospital ID: {}", hospitalId);

        List<Department> departments = departmentRepository.findByHospitalId(hospitalId);
        List<DepartmentPerformanceDto> performanceList = new ArrayList<>();

        for (Department department : departments) {
            DepartmentPerformanceDto performance = calculateDepartmentPerformance(department);
            performanceList.add(performance);
        }

        log.info("Department performance metrics calculated for {} departments", performanceList.size());

        return DepartmentPerformanceResponseDto.builder()
                .departments(performanceList)
                .build();
    }

    private DepartmentPerformanceDto calculateDepartmentPerformance(Department department) {
        Long departmentId = department.getId();

        // Calculate today's patients
        Long todayPatients = appointmentRepository.countTodayPatientsByDepartmentId(departmentId);

        // Calculate monthly patients
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.plusMonths(1).atDay(1).atStartOfDay();
        Long monthlyPatients = appointmentRepository.countMonthlyPatientsByDepartmentId(
                departmentId, startOfMonth, endOfMonth);

        // Count active doctors
        Long activeDoctors = userRepository.countActiveDoctorsByDepartmentId(departmentId);

        // Calculate average wait time
        Double avgWaitTime = calculateAverageWaitTime(departmentId);

        // Calculate patient satisfaction
        Double patientSatisfaction = calculatePatientSatisfaction(departmentId);

        log.debug("Department {} metrics - Today: {}, Monthly: {}, Doctors: {}, Wait: {}min, Satisfaction: {}",
                department.getName(), todayPatients, monthlyPatients, activeDoctors, avgWaitTime, patientSatisfaction);

        return DepartmentPerformanceDto.builder()
                .departmentId(departmentId)
                .name(department.getName())
                .todayPatients(todayPatients)
                .monthlyPatients(monthlyPatients)
                .activeDoctors(activeDoctors)
                .avgWaitTime(avgWaitTime)
                .patientSatisfaction(patientSatisfaction)
                .build();
    }

    private Double calculateAverageWaitTime(Long departmentId) {
        // Get appointments from the last 30 days that have check-in times
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Appointment> appointmentsWithWaitTime = appointmentRepository
                .findAppointmentsWithWaitTimeByDepartmentId(departmentId, thirtyDaysAgo);

        if (appointmentsWithWaitTime.isEmpty()) {
            return 0.0;
        }

        long totalWaitTimeMinutes = 0;
        int validAppointments = 0;

        for (Appointment appointment : appointmentsWithWaitTime) {
            if (appointment.getCheckedInAt() != null && appointment.getStartTime() != null) {
                // Calculate wait time as difference between scheduled start time and actual check-in
                Duration waitTime = Duration.between(appointment.getStartTime(), appointment.getCheckedInAt());

                // Only consider positive wait times (cases where patients were late are ignored for this metric)
                if (!waitTime.isNegative()) {
                    totalWaitTimeMinutes += waitTime.toMinutes();
                    validAppointments++;
                }
            }
        }

        if (validAppointments == 0) {
            return 0.0;
        }

        return (double) totalWaitTimeMinutes / validAppointments;
    }

    private Double calculatePatientSatisfaction(Long departmentId) {
        // Since there's no patient feedback/rating system yet, calculate a mock satisfaction score
        // based on appointment completion rate and average wait time as a proxy

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        Long completedAppointments = appointmentRepository
                .countCompletedAppointmentsByDepartmentId(departmentId, thirtyDaysAgo);
        Long totalAppointments = appointmentRepository
                .countTotalAppointmentsByDepartmentId(departmentId, thirtyDaysAgo);

        if (totalAppointments == 0) {
            return 0.0;
        }

        // Calculate completion rate (0.0 to 1.0)
        double completionRate = (double) completedAppointments / totalAppointments;

        // Get average wait time for this department
        Double avgWaitTime = calculateAverageWaitTime(departmentId);

        // Calculate satisfaction score (0.0 to 5.0)
        // Base score from completion rate (3.0 to 5.0 range)
        double baseScore = 3.0 + (completionRate * 2.0);

        // Adjust based on wait time (penalty for longer waits)
        double waitTimePenalty = 0.0;
        if (avgWaitTime > 30.0) {
            waitTimePenalty = Math.min(1.0, (avgWaitTime - 30.0) / 60.0); // Max penalty of 1.0 for 90+ min wait
        }

        double finalScore = Math.max(0.0, baseScore - waitTimePenalty);

        // Round to 1 decimal place
        return Math.round(finalScore * 10.0) / 10.0;
    }
}
