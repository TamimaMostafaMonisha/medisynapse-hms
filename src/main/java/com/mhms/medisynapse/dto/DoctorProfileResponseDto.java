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
public class DoctorProfileResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String status;
    private Boolean isActive;
    private Long hospitalId;
    private String hospitalName;
    private Long departmentId;
    private String departmentName;
    private Long appointmentCount;
    private Long completedAppointmentCount;
    private Long pendingAppointmentCount;
    private Long todayAppointmentCount;
    private Long upcomingAppointmentCount;
    private Long patientCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

