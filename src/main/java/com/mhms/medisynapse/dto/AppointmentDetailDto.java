package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDetailDto {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long hospitalId;
    private LocalDate date;
    private LocalTime time;
    private Integer duration;
    private String type;
    private String status;
    private String reason;
    private String notes;
    private LocalDateTime createdAt;
}

