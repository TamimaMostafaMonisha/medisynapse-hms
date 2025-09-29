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
public class HospitalListResponse {
    private boolean success;
    private List<HospitalDto> data;
    private PaginationInfo pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private int pageSize;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    public static HospitalListResponse success(List<HospitalDto> data, PaginationInfo pagination) {
        return HospitalListResponse.builder()
                .success(true)
                .data(data)
                .pagination(pagination)
                .build();
    }
}
