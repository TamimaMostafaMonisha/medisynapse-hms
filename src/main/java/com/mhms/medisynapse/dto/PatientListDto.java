package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientListDto {
    private Long id;
    private String name;
    private Integer age;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private String bloodGroup;
    private Long assignedDoctorId;
    private String assignedDoctorName;
    private String status;
    private String roomNumber;
    private LocalDateTime admissionDate;
    private EmergencyContactDto emergencyContact;
}
