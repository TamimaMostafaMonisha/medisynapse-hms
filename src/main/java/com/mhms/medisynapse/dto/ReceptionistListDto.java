package com.mhms.medisynapse.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReceptionistListDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String hospitalName;
    private Long hospitalId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private boolean deleted;
}
