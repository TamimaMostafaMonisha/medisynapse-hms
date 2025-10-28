package com.mhms.medisynapse.dto;

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
public class DoctorPatientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private Integer age;
    private Patient.Gender gender;
    private String phoneNumber;
    private String email;
    private String address;
    private String bloodGroup;
    private Long assignedDoctorId;
    private Long hospitalId;
    private String emergencyContactName;
    private String emergencyContactRelation;
    private String emergencyContactPhone;
    private LocalDateTime admissionDate;
    private String status;
    private String roomNumber;
    private String medicalHistory;
    private LocalDateTime lastVisitDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

