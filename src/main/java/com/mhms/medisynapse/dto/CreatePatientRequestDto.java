package com.mhms.medisynapse.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientRequestDto {

    @NotBlank(message = "Patient name is required")
    private String name;

    @NotNull(message = "Age is required")
    @Positive(message = "Age must be positive")
    private Integer age;

    @NotBlank(message = "Gender is required")
    private String gender; // MALE, FEMALE, OTHER

    @NotBlank(message = "Phone number is required")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String address;

    private String bloodGroup;

    @NotNull(message = "Hospital ID is required")
    private Long hospitalId;

    @Valid
    private EmergencyContactDto emergencyContact;

    private List<String> medicalHistory;

    // Optional fields that can be provided instead of age
    private LocalDate dateOfBirth;

    @NotBlank(message = "National ID is required")
    private String nationalId;
}
