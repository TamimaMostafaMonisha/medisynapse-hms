package com.mhms.medisynapse.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReceptionistPagedResponseDto {
    private List<ReceptionistListDto> data;
    private PaginationInfo pagination;
}

