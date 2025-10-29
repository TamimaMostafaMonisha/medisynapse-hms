package com.mhms.medisynapse.dto;

import com.mhms.medisynapse.entity.Appointment;
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
public class AppointmentFilterDto {
    private Long hospitalId;
    private Long doctorId;
    private Long patientId;
    private Long departmentId;
    private List<Appointment.AppointmentStatus> status;
    private Appointment.AppointmentType appointmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 20;
    @Builder.Default
    private String sortBy = "startTime";
    @Builder.Default
    private String sortDirection = "DESC";
}
