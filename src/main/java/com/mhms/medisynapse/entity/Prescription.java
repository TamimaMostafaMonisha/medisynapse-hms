package com.mhms.medisynapse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescription")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_patient_id", nullable = false, referencedColumnName = "id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_doctor_id", nullable = false, referencedColumnName = "id")
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_hospital_id", nullable = false, referencedColumnName = "id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_appointment_id", referencedColumnName = "id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_ehr_id", referencedColumnName = "id")
    private Ehr ehr;

    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate;

    @Column(name = "medication_name", nullable = false)
    private String medicationName;

    @Column(name = "dosage", length = 100)
    private String dosage;

    @Column(name = "frequency", length = 100)
    private String frequency;

    @Column(name = "duration", length = 100)
    private String duration;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // NEW FIELDS for Lab Test Workflow
    @Enumerated(EnumType.STRING)
    @Column(name = "prescription_type")
    private PrescriptionType prescriptionType = PrescriptionType.FINAL;

    @Column(name = "clinical_diagnosis", length = 500)
    private String clinicalDiagnosis;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "superseded_by")
    private Long supersededBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "refills_allowed")
    private Integer refillsAllowed = 0;

    @Column(name = "is_generic_allowed")
    private Boolean isGenericAllowed = true;

    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "last_updated_dt")
    private LocalDateTime lastUpdatedDt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "version")
    private Integer version = 1;

    @PrePersist
    protected void onCreate() {
        createdDt = LocalDateTime.now();
        lastUpdatedDt = LocalDateTime.now();
        if (prescriptionDate == null) {
            prescriptionDate = LocalDate.now();
        }
        if (status == null) {
            status = PrescriptionStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedDt = LocalDateTime.now();
    }

    @Getter
    public enum PrescriptionStatus {
        ACTIVE("Active"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled"),
        EXPIRED("Expired"),
        SUPERSEDED("Superseded");

        private final String displayName;

        PrescriptionStatus(String displayName) {
            this.displayName = displayName;
        }
    }

    @Getter
    public enum PrescriptionType {
        PRELIMINARY("Preliminary - Pending Lab Results"),
        FINAL("Final Prescription");

        private final String displayName;

        PrescriptionType(String displayName) {
            this.displayName = displayName;
        }
    }
}
