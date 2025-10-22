package com.mhms.medisynapse.dto;

import com.mhms.medisynapse.entity.Appointment;
import com.mhms.medisynapse.entity.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private Long id;

    // Comprehensive Patient Details
    private Long patientId;
    private String patientFirstName;
    private String patientLastName;
    private String patientFullName;
    private String patientNationalId;
    private LocalDate patientDob;
    private Patient.Gender patientGender;
    private String patientContact;
    private String patientEmail;
    private String patientBloodGroup;
    private String patientMedicalHistory;
    private String patientEmergencyContactName;
    private String patientEmergencyContactRelation;
    private String patientEmergencyContactPhone;
    private Patient.PatientStatus patientStatus;
    private String patientAddress;

    // Doctor Details
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private String doctorContact;
    private String doctorEmail;

    // Department Details
    private Long departmentId;
    private String departmentName;

    // Hospital Details
    private Long hospitalId;
    private String hospitalName;

    // Appointment Details
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Appointment.AppointmentType appointmentType;
    private Appointment.AppointmentStatus status;
    private String notes;
    private String reason;
    private String cancellationReason;

    // Reminder and Status Tracking
    private Boolean reminderSent;
    private LocalDateTime reminderSentAt;
    private LocalDateTime checkedInAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    // Recurring Appointment Details
    private Boolean isRecurring;
    private String recurringPattern;
    private Long parentAppointmentId;

    // Audit Fields
    private LocalDateTime createdDt;
    private LocalDateTime lastUpdatedDt;
    private Long createdBy;
    private Long updatedBy;
    private Integer version;
}
