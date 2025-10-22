package com.mhms.medisynapse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BedManagementResponseDto {
    private BedSummaryDto bedSummary;
    private List<DepartmentRoomsDto> roomsByDepartment;
}
