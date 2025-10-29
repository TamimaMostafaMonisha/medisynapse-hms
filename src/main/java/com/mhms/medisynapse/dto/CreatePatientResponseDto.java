package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientResponseDto {
    private Long id;
    private String name;
    private Integer age;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private String bloodGroup;
    private String status;
    private EmergencyContactDto emergencyContact;
    private String nationalId;
}
