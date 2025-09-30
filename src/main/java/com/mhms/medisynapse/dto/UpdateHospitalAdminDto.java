package com.mhms.medisynapse.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHospitalAdminDto {

    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Email(message = "Please provide a valid email address")
    private String email;

    //    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Please provide a valid phone number")
    private String phone;

    @Size(max = 50, message = "National ID must not exceed 50 characters")
    private String nationalId;

    private Long hospitalId;

    @Pattern(regexp = "ACTIVE|INACTIVE|SUSPENDED", message = "Status must be ACTIVE, INACTIVE, or SUSPENDED")
    private String status;
}
