package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDto {
    private Long id;

    @NotBlank(message = "Hospital name is required")
    @Size(min = 2, max = 100, message = "Hospital name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Hospital type is required")
    private String type;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be 10-15 digits, optionally starting with +")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Total beds is required")
    @Min(value = 1, message = "Total beds must be at least 1")
    private Integer totalBeds;

    @NotNull(message = "Available beds is required")
    @Min(value = 0, message = "Available beds cannot be negative")
    private Integer availableBeds;

    @NotNull(message = "Total departments is required")
    @Min(value = 1, message = "Total departments must be at least 1")
    private Integer totalDepartments;

    @NotNull(message = "Total staff is required")
    @Min(value = 1, message = "Total staff must be at least 1")
    private Integer totalStaff;

    @NotBlank(message = "Established year is required")
    @Pattern(regexp = "^(19[0-9]{2}|20[0-2][0-9])$", message = "Established year must be between 1900 and 2024")
    private String established;

    @NotBlank(message = "Accreditation is required")
    private String accreditation;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(Active|Under Maintenance|Inactive)$", message = "Status must be Active, Under Maintenance, or Inactive")
    private String status;

    private Long adminId;

    private List<Long> selectedDepartments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Legacy fields for backward compatibility
    private String contact;
    private AddressDto addressDto;
    private LocalDateTime createdDt;
    private LocalDateTime lastUpdatedDt;

    @AssertTrue(message = "Available beds cannot exceed total beds")
    public boolean isAvailableBedsValid() {
        if (totalBeds == null || availableBeds == null) {
            return true; // Let @NotNull handle null validation
        }
        return availableBeds <= totalBeds;
    }
}
