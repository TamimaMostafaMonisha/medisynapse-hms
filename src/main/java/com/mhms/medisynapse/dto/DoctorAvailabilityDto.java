package com.mhms.medisynapse.dto;

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
public class DoctorAvailabilityDto {
    private Long doctorId;
    private String doctorName;
    private LocalDate date;
    private List<TimeSlotDto> availableSlots;
    private List<TimeSlotDto> bookedSlots;
    private String workingHours;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlotDto {
        private String startTime;
        private String endTime;
        private Boolean available;
        private String reason;
        private Long appointmentId;
        private String patientName;
    }
}
