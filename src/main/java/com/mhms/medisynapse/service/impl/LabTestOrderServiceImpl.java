package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.LabResultReviewRequest;
import com.mhms.medisynapse.dto.LabResultUpdateRequest;
import com.mhms.medisynapse.dto.LabTestOrderRequest;
import com.mhms.medisynapse.dto.LabTestOrderResponse;
import com.mhms.medisynapse.entity.Appointment;
import com.mhms.medisynapse.entity.LabTestMaster;
import com.mhms.medisynapse.entity.LabTestOrder;
import com.mhms.medisynapse.entity.LabTestOrder.LabTestStatus;
import com.mhms.medisynapse.entity.LabTestOrder.TestUrgency;
import com.mhms.medisynapse.entity.Patient;
import com.mhms.medisynapse.entity.User;
import com.mhms.medisynapse.exception.ResourceNotFoundException;
import com.mhms.medisynapse.repository.AppointmentRepository;
import com.mhms.medisynapse.repository.HospitalRepository;
import com.mhms.medisynapse.repository.LabTestMasterRepository;
import com.mhms.medisynapse.repository.LabTestOrderRepository;
import com.mhms.medisynapse.repository.PatientRepository;
import com.mhms.medisynapse.repository.UserRepository;
import com.mhms.medisynapse.service.LabTestOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LabTestOrderServiceImpl implements LabTestOrderService {

    private final LabTestOrderRepository labTestOrderRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final LabTestMasterRepository labTestMasterRepository;

    @Override
    @Transactional
    public List<LabTestOrderResponse> createLabTestOrders(Long appointmentId, List<LabTestOrderRequest> requests) {
        log.info("Creating {} lab test orders for appointment: {}", requests.size(), appointmentId);

        // Fetch appointment to get hospital
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        List<LabTestOrder> orders = requests.stream()
                .map(request -> {
                    // Fetch entities
                    Patient patient = patientRepository.findById(request.getPatientId())
                            .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
                    User doctor = userRepository.findById(request.getDoctorId())
                            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

                    // Find lab test from master catalog
                    LabTestMaster labTestMaster = labTestMasterRepository.findByTestName(request.getTestName())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Lab test not found in catalog: '" + request.getTestName() + "'. " +
                                            "Please use GET /api/v1/lab-tests/available to see all available tests, " +
                                            "or GET /api/v1/lab-tests/search?query=<search> to search for tests."));

                    // Create lab test order
                    LabTestOrder order = LabTestOrder.builder()
                            .patient(patient)
                            .appointment(appointment)
                            .doctor(doctor)
                            .hospital(appointment.getHospital())
                            .labTestMaster(labTestMaster)
                            .urgency(TestUrgency.valueOf(request.getUrgency()))
                            .clinicalNotes(request.getClinicalNotes())
                            .suspectedDiagnosis(request.getSuspectedDiagnosis())
                            .status(LabTestStatus.ORDERED)
                            .createdBy(request.getDoctorId())
                            .isActive(true)
                            .build();

                    return order;
                })
                .collect(Collectors.toList());

        List<LabTestOrder> savedOrders = labTestOrderRepository.saveAll(orders);
        log.info("Successfully created {} lab test orders", savedOrders.size());

        return savedOrders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabTestOrderResponse> getLabOrdersForAppointment(Long appointmentId) {
        log.info("Fetching lab orders for appointment: {}", appointmentId);
        List<LabTestOrder> orders = labTestOrderRepository.findByAppointmentIdOrderByOrderedAtDesc(appointmentId);
        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabTestOrderResponse> getPendingLabResultsForDoctor(Long doctorId) {
        log.info("Fetching pending lab results for doctor: {}", doctorId);
        List<LabTestOrder> orders = labTestOrderRepository.findPendingReviewByDoctor(doctorId);
        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LabTestOrderResponse updateLabTestStatus(Long labOrderId, LabResultUpdateRequest request) {
        log.info("Updating lab test status for order: {}", labOrderId);

        LabTestOrder order = labTestOrderRepository.findById(labOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Lab test order not found with id: " + labOrderId));

        LabTestStatus newStatus = LabTestStatus.valueOf(request.getStatus());
        order.setStatus(newStatus);

        // Update timestamps based on status
        switch (newStatus) {
            case SAMPLE_COLLECTED:
                order.setSampleCollectedAt(LocalDateTime.now());
                break;
            case COMPLETED:
                order.setCompletedAt(LocalDateTime.now());
                if (request.getReportFileUrl() != null) {
                    order.setReportFileUrl(request.getReportFileUrl());
                    order.setUploadedAt(LocalDateTime.now());
                }
                break;
            case IN_PROGRESS:
                // Just update status
                break;
            default:
                break;
        }

        LabTestOrder savedOrder = labTestOrderRepository.save(order);
        log.info("Lab test status updated successfully");

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional
    public LabTestOrderResponse markLabResultAsReviewed(Long labResultId, Long doctorId, LabResultReviewRequest request) {
        log.info("Marking lab result as reviewed: {}", labResultId);

        LabTestOrder order = labTestOrderRepository.findById(labResultId)
                .orElseThrow(() -> new ResourceNotFoundException("Lab result not found with id: " + labResultId));

        order.setStatus(LabTestStatus.REVIEWED);
        order.setReviewedAt(LocalDateTime.now());
        order.setReviewedBy(doctorId);
        order.setUpdatedBy(doctorId);

        LabTestOrder savedOrder = labTestOrderRepository.save(order);
        log.info("Lab result marked as reviewed successfully");

        return mapToResponse(savedOrder);
    }

    @Override
    public List<LabTestOrderResponse> getLabOrdersByHospitalAndStatus(Long hospitalId, List<String> statuses) {
        log.info("Fetching lab orders for hospital: {} with statuses: {}", hospitalId, statuses);

        List<LabTestStatus> statusEnums = statuses.stream()
                .map(LabTestStatus::valueOf)
                .collect(Collectors.toList());

        List<LabTestOrder> orders = labTestOrderRepository.findByHospitalAndStatuses(hospitalId, statusEnums);

        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper method to map entity to response
    private LabTestOrderResponse mapToResponse(LabTestOrder order) {
        return LabTestOrderResponse.builder()
                .id(order.getId())
                .patientId(order.getPatient().getId())
                .appointmentId(order.getAppointment().getId())
                .doctorId(order.getDoctor().getId())
                .prescriptionId(order.getPrescription() != null ? order.getPrescription().getId() : null)
                .testName(order.getLabTestMaster().getTestName())
                .testType(order.getLabTestMaster().getTestType())
                .urgency(order.getUrgency().toString())
                .status(order.getStatus().toString())
                .clinicalNotes(order.getClinicalNotes())
                .suspectedDiagnosis(order.getSuspectedDiagnosis())
                .orderedAt(order.getOrderedAt())
                .sampleCollectedAt(order.getSampleCollectedAt())
                .completedAt(order.getCompletedAt())
                .reviewedAt(order.getReviewedAt())
                .reviewedBy(order.getReviewedBy())
                .reportFileUrl(order.getReportFileUrl())
                .uploadedBy(order.getUploadedBy())
                .uploadedAt(order.getUploadedAt())
                .build();
    }
}

