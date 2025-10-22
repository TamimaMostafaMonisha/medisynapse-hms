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
public class UpdatePatientResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String nationalId;
    private LocalDate dob;
    private Patient.Gender gender;
    private String contact;
    private String email;
    private String bloodGroup;
    private String emergencyContactName;
    private String emergencyContactRelation;
    private String emergencyContactPhone;
    private String medicalHistory;
    private AddressDto address;
    private Patient.PatientStatus status;
    private LocalDateTime lastUpdatedDt;
    private Long updatedBy;
}
