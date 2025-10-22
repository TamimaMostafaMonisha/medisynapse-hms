package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    private Long id;
    private String name;
    private String specialty;
    private String department;
    private String phone;
    private String email;
    private Long hospitalId;
    private String status;
    private String availabilityStatus;
    private Integer todayAppointments;
    private Map<String, ScheduleDto> schedule;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleDto {
        private String start;
        private String end;
    }
}
