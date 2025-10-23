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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_hospital",
        uniqueConstraints = @UniqueConstraint(name = "unique_patient_hospital",
                columnNames = {"fk_patient_id", "fk_hospital_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"patient", "hospital"})
public class PatientHospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_patient_id", nullable = false, referencedColumnName = "id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_hospital_id", nullable = false, referencedColumnName = "id")
    private Hospital hospital;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "patient_id_number", length = 50)
    private String patientIdNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PatientHospitalStatus status = PatientHospitalStatus.ACTIVE;

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
        if (registrationDate == null) {
            registrationDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedDt = LocalDateTime.now();
    }

    public enum PatientHospitalStatus {
        ACTIVE, INACTIVE, TRANSFERRED
    }
}
