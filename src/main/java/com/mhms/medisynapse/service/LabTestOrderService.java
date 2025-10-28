package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.LabResultReviewRequest;
import com.mhms.medisynapse.dto.LabResultUpdateRequest;
import com.mhms.medisynapse.dto.LabTestOrderRequest;
import com.mhms.medisynapse.dto.LabTestOrderResponse;

import java.util.List;

public interface LabTestOrderService {

    /**
     * Create lab test orders for an appointment
     */
    List<LabTestOrderResponse> createLabTestOrders(Long appointmentId, List<LabTestOrderRequest> requests);

    /**
     * Get lab orders for an appointment
     */
    List<LabTestOrderResponse> getLabOrdersForAppointment(Long appointmentId);

    /**
     * Get pending lab results for doctor review
     */
    List<LabTestOrderResponse> getPendingLabResultsForDoctor(Long doctorId);

    /**
     * Update lab test status (for hospital admin)
     */
    LabTestOrderResponse updateLabTestStatus(Long labOrderId, LabResultUpdateRequest request);

    /**
     * Mark lab result as reviewed
     */
    LabTestOrderResponse markLabResultAsReviewed(Long labResultId, Long doctorId, LabResultReviewRequest request);

    /**
     * Get lab orders by hospital and status (for hospital admin)
     */
    List<LabTestOrderResponse> getLabOrdersByHospitalAndStatus(Long hospitalId, List<String> statuses);
}

