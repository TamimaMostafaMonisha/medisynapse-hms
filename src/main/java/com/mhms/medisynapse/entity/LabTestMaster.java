package com.mhms.medisynapse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lab_test_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LabTestMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_name", nullable = false, unique = true)
    private String testName;

    @Column(name = "test_code", length = 50, unique = true)
    private String testCode;

    @Column(name = "test_type", nullable = false)
    private String testType;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "typical_turnaround_hours")
    private Integer typicalTurnaroundHours;

    @Column(name = "requires_fasting")
    private Boolean requiresFasting = false;

    @Column(name = "sample_type", length = 100)
    private String sampleType;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "last_updated_dt")
    private LocalDateTime lastUpdatedDt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Builder.Default
    @Version
    @Column(name = "version")
    private Integer version = 1;

    @PrePersist
    protected void onCreate() {
        this.createdDt = LocalDateTime.now();
        this.lastUpdatedDt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedDt = LocalDateTime.now();
    }
}

