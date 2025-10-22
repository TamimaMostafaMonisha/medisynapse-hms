package com.mhms.medisynapse.dto;

import com.mhms.medisynapse.entity.Patient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePatientRequestDto {

    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Size(max = 50, message = "National ID must not exceed 50 characters")
    private String nationalId;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    private Patient.Gender gender;

    @Size(max = 100, message = "Contact must not exceed 100 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]*$", message = "Invalid contact format")
    private String contact;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(max = 10, message = "Blood group must not exceed 10 characters")
    private String bloodGroup;

    @Size(max = 255, message = "Emergency contact name must not exceed 255 characters")
    private String emergencyContactName;

    @Size(max = 100, message = "Emergency contact relation must not exceed 100 characters")
    private String emergencyContactRelation;

    @Size(max = 20, message = "Emergency contact phone must not exceed 20 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]*$", message = "Invalid emergency contact phone format")
    private String emergencyContactPhone;

    private String medicalHistory;

    private AddressDto address;

    private Patient.PatientStatus status;
}
