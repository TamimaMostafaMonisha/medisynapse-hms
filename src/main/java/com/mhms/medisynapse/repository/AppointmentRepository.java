package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Existing queries
    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.hospital.id = :hospitalId AND a.status IN ('SCHEDULED', 'CONFIRMED')")
    Long countActiveAppointmentsByHospitalId(@Param("hospitalId") Long hospitalId);

    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.department.id = :departmentId " +
            "AND DATE(a.startTime) = CURRENT_DATE " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED', 'COMPLETED') " +
            "AND a.isActive = true")
    Long countTodayAppointmentsByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.patient.id = :patientId " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
            "AND a.isActive = true")
    Long countActiveAppointmentsByPatientId(@Param("patientId") Long patientId);

    // Enhanced doctor availability queries
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId AND DATE(a.startTime) = :date " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS') " +
            "AND a.isActive = true " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findAppointmentsByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    // Comprehensive appointment filtering
    @Query("SELECT a FROM Appointment a " +
            "WHERE (:hospitalId IS NULL OR a.hospital.id = :hospitalId) " +
            "AND (:doctorId IS NULL OR a.doctor.id = :doctorId) " +
            "AND (:patientId IS NULL OR a.patient.id = :patientId) " +
            "AND (:departmentId IS NULL OR a.department.id = :departmentId) " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:appointmentType IS NULL OR a.appointmentType = :appointmentType) " +
            "AND (:startDate IS NULL OR a.startTime >= :startDate) " +
            "AND (:endDate IS NULL OR a.startTime <= :endDate) " +
            "AND a.isActive = true " +
            "ORDER BY a.startTime DESC")
    Page<Appointment> findAppointmentsWithFilters(
            @Param("hospitalId") Long hospitalId,
            @Param("doctorId") Long doctorId,
            @Param("patientId") Long patientId,
            @Param("departmentId") Long departmentId,
            @Param("status") Appointment.AppointmentStatus status,
            @Param("appointmentType") Appointment.AppointmentType appointmentType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE " +
            "(:hospitalId IS NULL OR a.hospital.id = :hospitalId) AND " +
            "(:doctorId IS NULL OR a.doctor.id = :doctorId) AND " +
            "(:patientId IS NULL OR a.patient.id = :patientId) AND " +
            "(:departmentId IS NULL OR a.department.id = :departmentId) AND " +
            "(:appointmentType IS NULL OR a.appointmentType = :appointmentType) AND " +
            "(:startDate IS NULL OR a.startTime >= :startDate) AND " +
            "(:endDate IS NULL OR a.startTime <= :endDate) AND " +
            "(:statusList IS NULL OR a.status IN :statusList)")
    Page<Appointment> findAppointmentsWithFilters(
            @Param("hospitalId") Long hospitalId,
            @Param("doctorId") Long doctorId,
            @Param("patientId") Long patientId,
            @Param("departmentId") Long departmentId,
            @Param("appointmentType") Appointment.AppointmentType appointmentType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            @Param("statusList") List<Appointment.AppointmentStatus> statusList,
            Pageable pageable);

    // Doctor availability check for time conflicts
    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.id != :excludeAppointmentId " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS') " +
            "AND a.isActive = true " +
            "AND ((a.startTime < :endTime AND a.endTime > :startTime))")
    Long countConflictingAppointments(
            @Param("doctorId") Long doctorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeAppointmentId") Long excludeAppointmentId);

    // Upcoming appointments
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.hospital.id = :hospitalId " +
            "AND a.startTime >= :fromTime " +
            "AND a.startTime <= :toTime " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
            "AND a.isActive = true " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findUpcomingAppointments(
            @Param("hospitalId") Long hospitalId,
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime);

    // Today's appointments for a doctor (ALL statuses including COMPLETED)
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND DATE(a.startTime) = CURRENT_DATE " +
            "AND a.isActive = true " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findTodayAppointmentsByDoctor(@Param("doctorId") Long doctorId);

    // Patient appointment history
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patient.id = :patientId " +
            "AND a.hospital.id = :hospitalId " +
            "AND a.isActive = true " +
            "ORDER BY a.startTime DESC")
    Page<Appointment> findPatientAppointmentHistory(
            @Param("patientId") Long patientId,
            @Param("hospitalId") Long hospitalId,
            Pageable pageable);

    // Appointments requiring reminders
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.startTime BETWEEN :startTime AND :endTime " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
            "AND a.reminderSent = false " +
            "AND a.isActive = true")
    List<Appointment> findAppointmentsForReminder(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // Waiting list - cancelled appointments in time slots
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND DATE(a.startTime) = :date " +
            "AND a.status = 'CANCELLED' " +
            "AND a.isActive = true " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findCancelledAppointmentSlots(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date);

    // Statistics queries
    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.hospital.id = :hospitalId " +
            "AND a.startTime >= :startDate " +
            "AND a.startTime < :endDate " +
            "AND a.status = :status " +
            "AND a.isActive = true")
    Long countAppointmentsByStatusAndDateRange(
            @Param("hospitalId") Long hospitalId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") Appointment.AppointmentStatus status);

    // Find appointment by ID and hospital for security
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.id = :appointmentId " +
            "AND a.hospital.id = :hospitalId " +
            "AND a.isActive = true")
    Optional<Appointment> findByIdAndHospitalId(
            @Param("appointmentId") Long appointmentId,
            @Param("hospitalId") Long hospitalId);

    // Check if patient and doctor belong to same hospital
    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.patient.id = :patientId " +
            "AND a.doctor.id = :doctorId " +
            "AND a.hospital.id = :hospitalId")
    Long validatePatientDoctorHospitalRelation(
            @Param("patientId") Long patientId,
            @Param("doctorId") Long doctorId,
            @Param("hospitalId") Long hospitalId);

    // Bulk operations support
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.id IN :appointmentIds " +
            "AND a.hospital.id = :hospitalId " +
            "AND a.isActive = true")
    List<Appointment> findAppointmentsByIdsAndHospital(
            @Param("appointmentIds") List<Long> appointmentIds,
            @Param("hospitalId") Long hospitalId);

    // Department Performance Queries
    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.department.id = :departmentId " +
            "AND DATE(a.startTime) = CURRENT_DATE " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED') " +
            "AND a.isActive = true")
    Long countTodayPatientsByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.department.id = :departmentId " +
            "AND a.startTime >= :startOfMonth " +
            "AND a.startTime < :endOfMonth " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED') " +
            "AND a.isActive = true")
    Long countMonthlyPatientsByDepartmentId(
            @Param("departmentId") Long departmentId,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.department.id = :departmentId " +
            "AND a.checkedInAt IS NOT NULL " +
            "AND a.startTime >= :fromDate " +
            "AND a.isActive = true")
    List<Appointment> findAppointmentsWithWaitTimeByDepartmentId(
            @Param("departmentId") Long departmentId,
            @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.department.id = :departmentId " +
            "AND a.status = 'COMPLETED' " +
            "AND a.startTime >= :fromDate " +
            "AND a.isActive = true")
    Long countCompletedAppointmentsByDepartmentId(
            @Param("departmentId") Long departmentId,
            @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.department.id = :departmentId " +
            "AND a.startTime >= :fromDate " +
            "AND a.isActive = true")
    Long countTotalAppointmentsByDepartmentId(
            @Param("departmentId") Long departmentId,
            @Param("fromDate") LocalDateTime fromDate);

    // Doctor-specific queries
    @Query("SELECT COUNT(DISTINCT a.patient.id) FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.isActive = true")
    Long countDistinctPatientsByDoctor(@Param("doctorId") Long doctorId);

    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.status = 'COMPLETED' " +
            "AND a.isActive = true")
    Long countCompletedAppointmentsByDoctor(@Param("doctorId") Long doctorId);

    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
            "AND a.isActive = true")
    Long countPendingAppointmentsByDoctor(@Param("doctorId") Long doctorId);

    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND DATE(a.startTime) = CURRENT_DATE " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS') " +
            "AND a.isActive = true")
    Long countTodayAppointmentsByDoctor(@Param("doctorId") Long doctorId);

    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.startTime > CURRENT_TIMESTAMP " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
            "AND a.isActive = true")
    Long countUpcomingAppointmentsByDoctor(@Param("doctorId") Long doctorId);

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient " +
            "LEFT JOIN FETCH a.department " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.startTime > :startDate " +
            "AND a.startTime < :endDate " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
            "AND a.isActive = true " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findUpcomingAppointmentsByDoctor(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient " +
            "LEFT JOIN FETCH a.department " +
            "WHERE a.doctor.id = :doctorId " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:startDate IS NULL OR DATE(a.startTime) >= :startDate) " +
            "AND (:endDate IS NULL OR DATE(a.startTime) <= :endDate) " +
            "AND a.isActive = true " +
            "ORDER BY a.startTime DESC")
    Page<Appointment> findAppointmentsByDoctorWithFilters(
            @Param("doctorId") Long doctorId,
            @Param("status") Appointment.AppointmentStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.id = :appointmentId " +
            "AND a.doctor.id = :doctorId " +
            "AND a.isActive = true")
    Optional<Appointment> findByIdAndDoctorId(
            @Param("appointmentId") Long appointmentId,
            @Param("doctorId") Long doctorId);

    @Query("SELECT MAX(a.completedAt) FROM Appointment a " +
            "WHERE a.patient.id = :patientId " +
            "AND a.doctor.id = :doctorId " +
            "AND a.status = 'COMPLETED' " +
            "AND a.isActive = true")
    LocalDateTime findLastVisitDateByPatientAndDoctor(
            @Param("patientId") Long patientId,
            @Param("doctorId") Long doctorId);

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient " +
            "LEFT JOIN FETCH a.doctor " +
            "WHERE a.id = :appointmentId " +
            "AND a.doctor.id = :doctorId " +
            "AND a.isActive = true")
    Optional<Appointment> findAppointmentDetailsById(
            @Param("appointmentId") Long appointmentId,
            @Param("doctorId") Long doctorId);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patient.id = :patientId " +
            "AND a.doctor.id = :doctorId " +
            "AND a.id != :excludeAppointmentId " +
            "AND a.status = 'COMPLETED' " +
            "AND a.isActive = true " +
            "ORDER BY a.completedAt DESC")
    List<Appointment> findPreviousAppointmentsByPatientAndDoctor(
            @Param("patientId") Long patientId,
            @Param("doctorId") Long doctorId,
            @Param("excludeAppointmentId") Long excludeAppointmentId,
            Pageable pageable);
}
