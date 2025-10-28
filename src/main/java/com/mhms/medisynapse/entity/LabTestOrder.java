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
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lab_test_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LabTestOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_patient_id", nullable = false, referencedColumnName = "id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_appointment_id", nullable = false, referencedColumnName = "id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_doctor_id", nullable = false, referencedColumnName = "id")
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_prescription_id", referencedColumnName = "id")
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_hospital_id", nullable = false, referencedColumnName = "id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_lab_test_master_id", nullable = false, referencedColumnName = "id")
    private LabTestMaster labTestMaster;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "urgency")
    private TestUrgency urgency = TestUrgency.ROUTINE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LabTestStatus status = LabTestStatus.ORDERED;

    @Column(name = "clinical_notes", columnDefinition = "TEXT")
    private String clinicalNotes;

    @Column(name = "suspected_diagnosis", length = 500)
    private String suspectedDiagnosis;

    @Column(name = "ordered_at")
    private LocalDateTime orderedAt;

    @Column(name = "sample_collected_at")
    private LocalDateTime sampleCollectedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "report_file_url", length = 500)
    private String reportFileUrl;

    @Column(name = "uploaded_by")
    private Long uploadedBy;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "last_updated_dt")
    private LocalDateTime lastUpdatedDt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @Version
    @Column(name = "version")
    private Integer version = 1;

    @PrePersist
    protected void onCreate() {
        this.orderedAt = LocalDateTime.now();
        this.createdDt = LocalDateTime.now();
        this.lastUpdatedDt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedDt = LocalDateTime.now();
    }

    // Enums
    @Getter
    public enum TestUrgency {
        ROUTINE("Routine"),
        URGENT("Urgent"),
        STAT("STAT (Immediate)");

        private final String displayName;

        TestUrgency(String displayName) {
            this.displayName = displayName;
        }
    }

    @Getter
    public enum LabTestStatus {
        ORDERED("Ordered"),
        SAMPLE_COLLECTED("Sample Collected"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        REVIEWED("Reviewed by Doctor"),
        CANCELLED("Cancelled");

        private final String displayName;

        LabTestStatus(String displayName) {
            this.displayName = displayName;
        }
    }
}

