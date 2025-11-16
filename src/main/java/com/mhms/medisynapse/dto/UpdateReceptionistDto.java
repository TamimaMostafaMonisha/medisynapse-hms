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
public class UpdateReceptionistDto {
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Email(message = "Please provide a valid email address")
    private String email;

    private String phone;

    @Size(max = 50, message = "National ID must not exceed 50 characters")
    private String nationalId;

    private Long hospitalId; // Allow moving between hospitals

    @Pattern(regexp = "ACTIVE|INACTIVE|SUSPENDED", message = "Status must be ACTIVE, INACTIVE, or SUSPENDED")
    private String status; // ACTIVE, INACTIVE, SUSPENDED
}
