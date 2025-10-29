package com.mhms.medisynapse.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PatientHistoryResponseDTO {
    private PatientInfoDTO patientInfo;
    private List<AppointmentDTO> appointments;
    private List<AdmissionDTO> admissions;
    private List<EhrDTO> ehrs;
    private List<PrescriptionDTO> prescriptions;
    private List<LabResultDTO> labResults;
    private List<AttachmentDTO> attachments;
    private List<InsuranceDTO> insurances;
    private List<BillingDTO> billings;
    private List<HospitalDTO> hospitals;

    @Data
    public static class PatientInfoDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private LocalDate dob;
        private String gender;
        private String contact;
        private String email;
        private String bloodGroup;
        private AddressDTO address;
        private List<EmergencyContactDTO> emergencyContacts;
        private String status;
        private LocalDateTime createdDt;
        private LocalDateTime lastUpdatedDt;
    }

    @Data
    public static class AddressDTO {
        private String line1;
        private String line2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String type;
    }

    @Data
    public static class EmergencyContactDTO {
        private String name;
        private String relation;
        private String phone;
    }

    @Data
    public static class AppointmentDTO {
        private Long id;
        private LocalDateTime dateTime;
        private DoctorDTO doctor;
        private DepartmentDTO department;
        private String status;
        private String type;
        private String reason;
    }

    @Data
    public static class AdmissionDTO {
        private Long id;
        private LocalDateTime admissionDate;
        private LocalDateTime dischargeDate;
        private HospitalDTO hospital;
        private DepartmentDTO department;
        private DoctorDTO admittingDoctor;
        private String status;
        private String reason;
    }

    @Data
    public static class EhrDTO {
        private Long id;
        private LocalDate visitDate;
        private DoctorDTO doctor;
        private DepartmentDTO department;
        private String diagnosis;
        private List<PrescriptionDTO> prescriptions;
        private List<AttachmentDTO> attachments;
        private LocalDateTime createdDt;
        private LocalDateTime lastUpdatedDt;
    }

    @Data
    public static class PrescriptionDTO {
        private Long id;
        private DoctorDTO doctor;
        private HospitalDTO hospital;
        private AppointmentDTO appointment;
        private List<MedicationDTO> medications;
        private String status;
        private LocalDateTime createdDt;
    }

    @Data
    public static class MedicationDTO {
        private String name;
        private String dose;
        private String frequency;
        private String duration;
        private String route;
    }

    @Data
    public static class LabResultDTO {
        private Long id;
        private String testName;
        private String result;
        private String unit;
        private String referenceRange;
        private LocalDateTime date;
        private DoctorDTO doctor;
    }

    @Data
    public static class AttachmentDTO {
        private Long id;
        private String type;
        private String url;
        private LocalDateTime date;
        private String description;
    }

    @Data
    public static class InsuranceDTO {
        private Long id;
        private String provider;
        private String policyNumber;
        private String status;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Data
    public static class BillingDTO {
        private Long id;
        private String type;
        private Double amount;
        private String status;
        private LocalDateTime date;
    }

    @Data
    public static class HospitalDTO {
        private Long id;
        private String name;
        private AddressDTO address;
    }

    @Data
    public static class DepartmentDTO {
        private Long id;
        private String name;
    }

    @Data
    public static class DoctorDTO {
        private Long id;
        private String name;
        private String specialization;
    }
}

