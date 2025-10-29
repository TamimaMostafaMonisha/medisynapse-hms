package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.AddressDto;
import com.mhms.medisynapse.dto.CreatePatientRequestDto;
import com.mhms.medisynapse.dto.CreatePatientResponseDto;
import com.mhms.medisynapse.dto.PaginationDto;
import com.mhms.medisynapse.dto.PatientHistoryResponseDTO;
import com.mhms.medisynapse.dto.PatientListDto;
import com.mhms.medisynapse.dto.PatientPagedResponseDto;
import com.mhms.medisynapse.dto.UpdatePatientRequestDto;
import com.mhms.medisynapse.dto.UpdatePatientResponseDto;
import com.mhms.medisynapse.entity.Address;
import com.mhms.medisynapse.entity.Hospital;
import com.mhms.medisynapse.entity.Patient;
import com.mhms.medisynapse.entity.PatientHospital;
import com.mhms.medisynapse.entity.PatientHospital.PatientHospitalStatus;
import com.mhms.medisynapse.entity.Prescription;
import com.mhms.medisynapse.repository.AddressRepository;
import com.mhms.medisynapse.repository.AdmissionRepository;
import com.mhms.medisynapse.repository.AppointmentRepository;
import com.mhms.medisynapse.repository.EhrRepository;
import com.mhms.medisynapse.repository.HospitalRepository;
import com.mhms.medisynapse.repository.PatientHospitalRepository;
import com.mhms.medisynapse.repository.PatientRepository;
import com.mhms.medisynapse.repository.PrescriptionRepository;
import com.mhms.medisynapse.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final AdmissionRepository admissionRepository;
    private final HospitalRepository hospitalRepository;
    private final AppointmentRepository appointmentRepository;
    private final AddressRepository addressRepository;
    private final PatientHospitalRepository patientHospitalRepository;
    private final EhrRepository ehrRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Override
    public PatientPagedResponseDto getPatientsByHospitalId(Long hospitalId,
                                                           Patient.PatientStatus status,
                                                           String search,
                                                           Pageable pageable) {
        log.info("Fetching patients for hospital ID: {}, status: {}, search: {}", hospitalId, status, search);

        Page<Patient> patientPage = patientRepository.findPatientsByHospitalId(hospitalId, status, search, pageable);

        List<PatientListDto> patientDtos = patientPage.getContent().stream()
                .map(this::convertToPatientListDto)
                .collect(Collectors.toList());

        PaginationDto pagination = PaginationDto.builder()
                .currentPage(patientPage.getNumber())
                .totalPages(patientPage.getTotalPages())
                .totalElements(patientPage.getTotalElements())
                .size(patientPage.getSize())
                .build();

        log.info("Retrieved {} patients for hospital {}", patientDtos.size(), hospitalId);

        return PatientPagedResponseDto.builder()
                .patients(patientDtos)
                .pagination(pagination)
                .build();
    }

    @Override
    public PatientListDto convertToPatientListDto(Patient patient) {
        // Calculate age from date of birth
        Integer age = null;
        if (patient.getDob() != null) {
            age = java.time.Period.between(patient.getDob(), java.time.LocalDate.now()).getYears();
        }
        String roomNumber = null;
        String admissionStatus = "Outpatient";
        Long assignedDoctorId = null;
        String assignedDoctorName = null;
        java.util.List<com.mhms.medisynapse.entity.PatientHospital> patientHospitals = new java.util.ArrayList<>();
        if (patient.getPatientHospitals() != null) {
            patientHospitals.addAll(patient.getPatientHospitals());
        }
        Long hospitalId = null;
        if (!patientHospitals.isEmpty() && patientHospitals.getFirst().getHospital() != null) {
            hospitalId = patientHospitals.getFirst().getHospital().getId();
        }
        java.util.List<com.mhms.medisynapse.entity.Admission> admissions = new java.util.ArrayList<>();
        if (hospitalId != null) {
            admissions.addAll(admissionRepository.findRecentAdmissionsByHospitalId(
                    hospitalId,
                    java.time.LocalDateTime.now().minusDays(30)
            ));
        }
        for (com.mhms.medisynapse.entity.Admission admission : admissions) {
            if (admission.getPatient() != null && admission.getPatient().getId().equals(patient.getId()) &&
                    "ADMITTED".equals(admission.getStatus().toString())) {
                roomNumber = admission.getBedNo();
                admissionStatus = "Admitted";
                if (admission.getAdmittingDoctor() != null) {
                    assignedDoctorId = admission.getAdmittingDoctor().getId();
                    assignedDoctorName = admission.getAdmittingDoctor().getName();
                }
                break;
            }
        }
        com.mhms.medisynapse.dto.EmergencyContactDto emergencyContact = com.mhms.medisynapse.dto.EmergencyContactDto.builder()
                .name(patient.getEmergencyContactName())
                .relation(patient.getEmergencyContactRelation())
                .phone(patient.getEmergencyContactPhone())
                .build();
        String addressString = null;
        if (patient.getAddress() != null) {
            addressString = patient.getAddress().getLine1();
            if (patient.getAddress().getLine2() != null && !patient.getAddress().getLine2().trim().isEmpty()) {
                addressString += ", " + patient.getAddress().getLine2();
            }
            if (patient.getAddress().getCity() != null) {
                addressString += ", " + patient.getAddress().getCity();
            }
        }
        return com.mhms.medisynapse.dto.PatientListDto.builder()
                .id(patient.getId())
                .name(patient.getFirstName() + " " + patient.getLastName())
                .age(age)
                .gender(capitalizeFirstLetter(patient.getGender().toString()))
                .phone(patient.getContact())
                .email(patient.getEmail())
                .address(addressString)
                .bloodGroup(patient.getBloodGroup())
                .assignedDoctorId(assignedDoctorId)
                .assignedDoctorName(assignedDoctorName)
                .status(admissionStatus)
                .roomNumber(roomNumber)
                .admissionDate(patient.getCreatedDt())
                .emergencyContact(emergencyContact)
                .build();
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    @Override
    @Transactional
    public CreatePatientResponseDto createPatient(CreatePatientRequestDto request) {
        log.info("Creating new patient: {}", request.getName());

        // Check for existing patient by nationalId
        if (patientRepository.findByNationalId(request.getNationalId()).isPresent()) {
            throw new RuntimeException("Patient with this nationalId is already registered");
        }

        // Validate hospital exists
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found with ID: " + request.getHospitalId()));

        // Parse name into firstName and lastName
        String[] nameParts = request.getName().trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Calculate date of birth from age if not provided
        LocalDate dateOfBirth = request.getDateOfBirth();
        if (dateOfBirth == null && request.getAge() != null) {
            dateOfBirth = LocalDate.now().minusYears(request.getAge());
        }

        // Parse gender
        Patient.Gender gender;
        try {
            gender = Patient.Gender.valueOf(request.getGender().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid gender: " + request.getGender() + ". Must be MALE, FEMALE, or OTHER");
        }

        // Create Address entity if address is provided
        Address address = null;
        if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
            address = new Address();
            address.setLine1(request.getAddress());
            address.setCity("Not Specified"); // Default value
            address.setCountry("Not Specified"); // Default value
            address.setType(Address.AddressType.HOME);
            address.setIsActive(true);
            // Manual persist: save address first
            address = addressRepository.save(address);
        }

        // Create Patient entity
        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setNationalId(request.getNationalId());
        patient.setDob(dateOfBirth);
        patient.setGender(gender);
        patient.setContact(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setAddress(address);
        patient.setStatus(Patient.PatientStatus.ACTIVE);
        patient.setIsActive(true);

        // Set emergency contact details if provided
        if (request.getEmergencyContact() != null) {
            patient.setEmergencyContactName(request.getEmergencyContact().getName());
            patient.setEmergencyContactRelation(request.getEmergencyContact().getRelation());
            patient.setEmergencyContactPhone(request.getEmergencyContact().getPhone());
        }

        // Save patient
        Patient savedPatient = patientRepository.save(patient);

        // Create PatientHospital association
        PatientHospital patientHospital = new PatientHospital();
        patientHospital.setPatient(savedPatient);
        patientHospital.setHospital(hospital);
        patientHospital.setRegistrationDate(LocalDate.now());
        patientHospital.setPatientIdNumber("PAT-" + savedPatient.getId());
        patientHospital.setStatus(PatientHospital.PatientHospitalStatus.ACTIVE);
        patientHospital.setIsActive(true);
        // Persist PatientHospital association
        patientHospitalRepository.save(patientHospital);

        // Calculate age for response
        Integer age = null;
        if (dateOfBirth != null) {
            age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        }

        // Create response
        CreatePatientResponseDto response = CreatePatientResponseDto.builder()
                .id(savedPatient.getId())
                .name(savedPatient.getFirstName() + " " + savedPatient.getLastName())
                .age(age)
                .gender(capitalizeFirstLetter(savedPatient.getGender().toString()))
                .phone(savedPatient.getContact())
                .email(request.getEmail())
                .address(request.getAddress())
                .bloodGroup(request.getBloodGroup())
                .status("Active")
                .emergencyContact(request.getEmergencyContact())
                .nationalId(savedPatient.getNationalId())
                .build();

        log.info("Successfully created patient with ID: {}", savedPatient.getId());
        return response;
    }

    @Override
    @Transactional
    public UpdatePatientResponseDto updatePatient(Long patientId, UpdatePatientRequestDto request, Long hospitalId) {
        log.info("Updating patient ID: {} for hospital ID: {}", patientId, hospitalId);

        // Validate ownership - patient belongs to admin's hospital
        Patient existingPatient = patientRepository.findPatientByIdAndHospitalId(patientId, hospitalId);
        if (existingPatient == null) {
            throw new RuntimeException("Patient not found or does not belong to this hospital");
        }

        // Update only provided fields
        if (request.getFirstName() != null) {
            existingPatient.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            existingPatient.setLastName(request.getLastName());
        }
        if (request.getNationalId() != null) {
            existingPatient.setNationalId(request.getNationalId());
        }
        if (request.getDob() != null) {
            existingPatient.setDob(request.getDob());
        }
        if (request.getGender() != null) {
            existingPatient.setGender(request.getGender());
        }
        if (request.getContact() != null) {
            existingPatient.setContact(request.getContact());
        }
        if (request.getEmail() != null) {
            existingPatient.setEmail(request.getEmail());
        }
        if (request.getBloodGroup() != null) {
            existingPatient.setBloodGroup(request.getBloodGroup());
        }
        if (request.getEmergencyContactName() != null) {
            existingPatient.setEmergencyContactName(request.getEmergencyContactName());
        }
        if (request.getEmergencyContactRelation() != null) {
            existingPatient.setEmergencyContactRelation(request.getEmergencyContactRelation());
        }
        if (request.getEmergencyContactPhone() != null) {
            existingPatient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        }
        if (request.getMedicalHistory() != null) {
            existingPatient.setMedicalHistory(request.getMedicalHistory());
        }
        if (request.getStatus() != null) {
            existingPatient.setStatus(request.getStatus());
        }

        // Handle address update if provided
        if (request.getAddress() != null) {
            if (existingPatient.getAddress() != null) {
                // Update existing address
                Address address = existingPatient.getAddress();
                if (request.getAddress().getLine1() != null) {
                    address.setLine1(request.getAddress().getLine1());
                }
                if (request.getAddress().getLine2() != null) {
                    address.setLine2(request.getAddress().getLine2());
                }
                if (request.getAddress().getCity() != null) {
                    address.setCity(request.getAddress().getCity());
                }
                if (request.getAddress().getState() != null) {
                    address.setState(request.getAddress().getState());
                }
                if (request.getAddress().getPostalCode() != null) {
                    address.setPostalCode(request.getAddress().getPostalCode());
                }
                if (request.getAddress().getCountry() != null) {
                    address.setCountry(request.getAddress().getCountry());
                }
            } else {
                // Create new address
                Address address = new Address();
                address.setLine1(request.getAddress().getLine1());
                address.setLine2(request.getAddress().getLine2());
                address.setCity(request.getAddress().getCity());
                address.setState(request.getAddress().getState());
                address.setPostalCode(request.getAddress().getPostalCode());
                address.setCountry(request.getAddress().getCountry());
                address.setType(Address.AddressType.HOME); // Set default type
                address.setIsActive(true); // Set active status
                existingPatient.setAddress(address);
            }
        }

        // Update audit fields
        existingPatient.setVersion(existingPatient.getVersion() + 1);
        // In a real application, you would get the current user ID from security context
        // existingPatient.setUpdatedBy(getCurrentUserId());

        Patient updatedPatient = patientRepository.save(existingPatient);

        log.info("Patient ID: {} updated successfully", patientId);

        return convertToUpdatePatientResponseDto(updatedPatient);
    }

    @Override
    @Transactional
    public void deletePatient(Long patientId, Long hospitalId) {
        log.info("Soft deleting patient ID: {} for hospital ID: {}", patientId, hospitalId);

        // Validate ownership - patient belongs to admin's hospital
        Patient existingPatient = patientRepository.findPatientByIdAndHospitalId(patientId, hospitalId);
        if (existingPatient == null) {
            throw new RuntimeException("Patient not found or does not belong to this hospital");
        }

        // Check for active appointments before deletion
        Long activeAppointmentsCount = appointmentRepository.countActiveAppointmentsByPatientId(patientId);
        if (activeAppointmentsCount > 0) {
            throw new RuntimeException("Cannot delete patient with active appointments. Please cancel or complete all appointments first.");
        }

        // Soft delete - change status and set inactive
        existingPatient.setStatus(Patient.PatientStatus.INACTIVE);
        existingPatient.setIsActive(false);

        // Update audit fields
        existingPatient.setVersion(existingPatient.getVersion() + 1);
        // In a real application, you would get the current user ID from security context
        // existingPatient.setUpdatedBy(getCurrentUserId());

        patientRepository.save(existingPatient);

        log.info("Patient ID: {} soft deleted successfully", patientId);
    }

    private UpdatePatientResponseDto convertToUpdatePatientResponseDto(Patient patient) {
        AddressDto addressDto = null;
        if (patient.getAddress() != null) {
            addressDto = AddressDto.builder()
                    .id(patient.getAddress().getId())
                    .line1(patient.getAddress().getLine1())
                    .line2(patient.getAddress().getLine2())
                    .city(patient.getAddress().getCity())
                    .state(patient.getAddress().getState())
                    .postalCode(patient.getAddress().getPostalCode())
                    .country(patient.getAddress().getCountry())
                    .build();
        }

        return UpdatePatientResponseDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .nationalId(patient.getNationalId())
                .dob(patient.getDob())
                .gender(patient.getGender())
                .contact(patient.getContact())
                .email(patient.getEmail())
                .bloodGroup(patient.getBloodGroup())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactRelation(patient.getEmergencyContactRelation())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .medicalHistory(patient.getMedicalHistory())
                .address(addressDto)
                .status(patient.getStatus())
                .lastUpdatedDt(patient.getLastUpdatedDt())
                .updatedBy(patient.getUpdatedBy())
                .build();
    }

    @Override
    public Page<Patient> getPatientsByHospitalAndStatus(Long hospitalId, PatientHospitalStatus status, Pageable pageable) {
        return patientRepository.findPatientsByHospitalAndStatus(hospitalId, status, pageable);
    }

    @Override
    public Page<Patient> getEligiblePatientsForHospital(Long hospitalId, Pageable pageable) {
        return patientRepository.findEligiblePatientsForHospital(hospitalId, pageable);
    }

    @Override
    public Page<Patient> getAllPatients(Boolean onlyActive, Pageable pageable) {
        if (Boolean.TRUE.equals(onlyActive)) {
            return patientRepository.findAllByIsActiveTrue(pageable);
        } else {
            return patientRepository.findAll(pageable);
        }
    }

    @Override
    public void assignPatientToHospital(Long patientId, Long hospitalId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        // Check global isActive
        if (!Boolean.TRUE.equals(patient.getIsActive())) {
            throw new RuntimeException("Patient is globally inactive and cannot be assigned");
        }
        // Find or create PatientHospital
        PatientHospital ph = patientHospitalRepository.findByPatientIdAndHospitalId(patientId, hospitalId)
                .orElseGet(() -> {
                    PatientHospital newPh = new PatientHospital();
                    newPh.setPatient(patient);
                    newPh.setHospital(hospitalRepository.findById(hospitalId).orElseThrow(() -> new RuntimeException("Hospital not found")));
                    newPh.setRegistrationDate(java.time.LocalDate.now());
                    newPh.setIsActive(true);
                    newPh.setStatus(PatientHospitalStatus.ACTIVE);
                    return newPh;
                });
        // Reactivate if needed
        ph.setIsActive(true);
        ph.setStatus(PatientHospitalStatus.ACTIVE);
        patientHospitalRepository.save(ph);
    }

    @Override
    public PatientHistoryResponseDTO getPatientFullHistory(Long patientId) {
        PatientHistoryResponseDTO dto = new PatientHistoryResponseDTO();

        // Fetch patient
        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null) {
            return dto;
        }

        // Map patient info
        PatientHistoryResponseDTO.PatientInfoDTO patientInfo = new PatientHistoryResponseDTO.PatientInfoDTO();
        patientInfo.setId(patient.getId());
        patientInfo.setFirstName(patient.getFirstName());
        patientInfo.setLastName(patient.getLastName());
        patientInfo.setDob(patient.getDob());
        patientInfo.setGender(patient.getGender() != null ? patient.getGender().toString() : null);
        patientInfo.setContact(patient.getContact());
        patientInfo.setEmail(patient.getEmail());
        patientInfo.setBloodGroup(patient.getBloodGroup());
        patientInfo.setStatus(patient.getStatus() != null ? patient.getStatus().toString() : null);
        patientInfo.setCreatedDt(patient.getCreatedDt());
        patientInfo.setLastUpdatedDt(patient.getLastUpdatedDt());
        // Address
        if (patient.getAddress() != null) {
            PatientHistoryResponseDTO.AddressDTO addressDTO = new PatientHistoryResponseDTO.AddressDTO();
            addressDTO.setLine1(patient.getAddress().getLine1());
            addressDTO.setLine2(patient.getAddress().getLine2());
            addressDTO.setCity(patient.getAddress().getCity());
            addressDTO.setState(patient.getAddress().getState());
            addressDTO.setPostalCode(patient.getAddress().getPostalCode());
            addressDTO.setCountry(patient.getAddress().getCountry());
            addressDTO.setType(patient.getAddress().getType() != null ? patient.getAddress().getType().toString() : null);
            patientInfo.setAddress(addressDTO);
        }
        // Emergency contacts
        PatientHistoryResponseDTO.EmergencyContactDTO ec = new PatientHistoryResponseDTO.EmergencyContactDTO();
        ec.setName(patient.getEmergencyContactName());
        ec.setRelation(patient.getEmergencyContactRelation());
        ec.setPhone(patient.getEmergencyContactPhone());
        patientInfo.setEmergencyContacts(
                (ec.getName() != null || ec.getPhone() != null) ? java.util.Collections.singletonList(ec) : java.util.Collections.emptyList()
        );
        dto.setPatientInfo(patientInfo);

        // Appointments
        dto.setAppointments(appointmentRepository.findAll().stream()
                .filter(a -> a.getPatient() != null && a.getPatient().getId().equals(patientId))
                .map(a -> {
                    PatientHistoryResponseDTO.AppointmentDTO adto = new PatientHistoryResponseDTO.AppointmentDTO();
                    adto.setId(a.getId());
                    adto.setDateTime(a.getStartTime()); // Use startTime as the appointment date/time
                    if (a.getDoctor() != null) {
                        PatientHistoryResponseDTO.DoctorDTO d = new PatientHistoryResponseDTO.DoctorDTO();
                        d.setId(a.getDoctor().getId());
                        d.setName(a.getDoctor().getName());
                        // Specialization not available in User entity; set as null or fetch from related entity if needed
                        d.setSpecialization(null);
                        adto.setDoctor(d);
                    }
                    if (a.getDepartment() != null) {
                        PatientHistoryResponseDTO.DepartmentDTO dep = new PatientHistoryResponseDTO.DepartmentDTO();
                        dep.setId(a.getDepartment().getId());
                        dep.setName(a.getDepartment().getName());
                        adto.setDepartment(dep);
                    }
                    adto.setStatus(a.getStatus() != null ? a.getStatus().toString() : null);
                    adto.setType(a.getAppointmentType() != null ? a.getAppointmentType().toString() : null);
                    adto.setReason(a.getReason());
                    return adto;
                }).toList());

        // Admissions
        dto.setAdmissions(admissionRepository.findAll().stream()
                .filter(ad -> ad.getPatient() != null && ad.getPatient().getId().equals(patientId))
                .map(ad -> {
                    PatientHistoryResponseDTO.AdmissionDTO adto = new PatientHistoryResponseDTO.AdmissionDTO();
                    adto.setId(ad.getId());
                    adto.setAdmissionDate(ad.getAdmissionDate());
                    adto.setDischargeDate(ad.getDischargeDt()); // Use correct field name
                    if (ad.getHospital() != null) {
                        PatientHistoryResponseDTO.HospitalDTO h = new PatientHistoryResponseDTO.HospitalDTO();
                        h.setId(ad.getHospital().getId());
                        h.setName(ad.getHospital().getName());
                        adto.setHospital(h);
                    }
                    if (ad.getDepartment() != null) {
                        PatientHistoryResponseDTO.DepartmentDTO dep = new PatientHistoryResponseDTO.DepartmentDTO();
                        dep.setId(ad.getDepartment().getId());
                        dep.setName(ad.getDepartment().getName());
                        adto.setDepartment(dep);
                    }
                    if (ad.getAdmittingDoctor() != null) {
                        PatientHistoryResponseDTO.DoctorDTO d = new PatientHistoryResponseDTO.DoctorDTO();
                        d.setId(ad.getAdmittingDoctor().getId());
                        d.setName(ad.getAdmittingDoctor().getName());
                        d.setSpecialization(null); // Specialization not available
                        adto.setAdmittingDoctor(d);
                    }
                    adto.setStatus(ad.getStatus() != null ? ad.getStatus().toString() : null);
                    adto.setReason(ad.getNotes()); // Use notes as the reason for admission
                    return adto;
                }).toList());

        // EHRs
        dto.setEhrs(ehrRepository.findAllByPatientId(patientId).stream().map(e -> {
            PatientHistoryResponseDTO.EhrDTO edto = new PatientHistoryResponseDTO.EhrDTO();
            edto.setId(e.getId());
            edto.setVisitDate(e.getVisitDate());
            if (e.getDoctor() != null) {
                PatientHistoryResponseDTO.DoctorDTO d = new PatientHistoryResponseDTO.DoctorDTO();
                d.setId(e.getDoctor().getId());
                d.setName(e.getDoctor().getName());
                d.setSpecialization(null); // Specialization not available
                edto.setDoctor(d);
            }
            if (e.getDepartment() != null) {
                PatientHistoryResponseDTO.DepartmentDTO dep = new PatientHistoryResponseDTO.DepartmentDTO();
                dep.setId(e.getDepartment().getId());
                dep.setName(e.getDepartment().getName());
                edto.setDepartment(dep);
            }
            edto.setDiagnosis(e.getDiagnosis());
            edto.setCreatedDt(e.getCreatedDt());
            edto.setLastUpdatedDt(e.getLastUpdatedDt());
            // TODO: Map prescriptions and attachments if needed
            return edto;
        }).toList());

        // Map prescriptions
        List<Prescription> prescriptions = prescriptionRepository.findByPatientAndHospital(patientId, null);
        dto.setPrescriptions(prescriptions.stream().map(p -> {
            PatientHistoryResponseDTO.PrescriptionDTO pdto = new PatientHistoryResponseDTO.PrescriptionDTO();
            pdto.setId(p.getId());
            pdto.setStatus(p.getStatus() != null ? p.getStatus().toString() : null);
            pdto.setCreatedDt(p.getCreatedDt());
            if (p.getDoctor() != null) {
                PatientHistoryResponseDTO.DoctorDTO d = new PatientHistoryResponseDTO.DoctorDTO();
                d.setId(p.getDoctor().getId());
                d.setName(p.getDoctor().getName());
                pdto.setDoctor(d);
            }
            if (p.getHospital() != null) {
                PatientHistoryResponseDTO.HospitalDTO h = new PatientHistoryResponseDTO.HospitalDTO();
                h.setId(p.getHospital().getId());
                h.setName(p.getHospital().getName());
                pdto.setHospital(h);
            }
            return pdto;
        }).toList());

        // Map attachments from all EHRs (fix: map from entity Attachment, not AttachmentDTO)
        List<PatientHistoryResponseDTO.AttachmentDTO> attachments = dto.getEhrs().stream()
            .flatMap(ehr -> {
                if (ehr.getId() == null) return java.util.stream.Stream.empty();
                // Find the EHR entity by id to get attachments
                com.mhms.medisynapse.entity.Ehr ehrEntity = ehrRepository.findById(ehr.getId()).orElse(null);
                if (ehrEntity == null || ehrEntity.getAttachments() == null) return java.util.stream.Stream.empty();
                return ehrEntity.getAttachments().stream();
            })
            .map(a -> {
                PatientHistoryResponseDTO.AttachmentDTO adto = new PatientHistoryResponseDTO.AttachmentDTO();
                adto.setId(a.getId());
                adto.setType(a.getType() != null ? a.getType().toString() : null);
                adto.setUrl(a.getFilePath());
                adto.setDate(a.getCreatedDt());
                adto.setDescription(null); // No description field in entity
                return adto;
            }).toList();
        dto.setAttachments(attachments);

        // Map insurances
        dto.setInsurances(patient.getPatientInsurances() != null ? patient.getPatientInsurances().stream().map(pi -> {
            PatientHistoryResponseDTO.InsuranceDTO idto = new PatientHistoryResponseDTO.InsuranceDTO();
            idto.setId(pi.getInsurance().getId());
            idto.setProvider(pi.getInsurance().getProvider());
            idto.setPolicyNumber(pi.getInsurance().getPolicyNumber());
            idto.setStatus(null); // No status field in entity
            idto.setStartDate(pi.getInsurance().getValidFrom());
            idto.setEndDate(pi.getInsurance().getValidTo());
            return idto;
        }).toList() : java.util.Collections.emptyList());

        // Map billings
        dto.setBillings(patient.getBillings() != null ? patient.getBillings().stream().map(b -> {
            PatientHistoryResponseDTO.BillingDTO bdto = new PatientHistoryResponseDTO.BillingDTO();
            bdto.setId(b.getId());
            bdto.setType(b.getPaymentMethod() != null ? b.getPaymentMethod().toString() : null);
            bdto.setAmount(b.getTotalAmount() != null ? b.getTotalAmount().doubleValue() : null);
            bdto.setStatus(b.getStatus() != null ? b.getStatus().toString() : null);
            bdto.setDate(b.getCreatedDt());
            return bdto;
        }).toList() : java.util.Collections.emptyList());

        // Map hospitals
        dto.setHospitals(patient.getPatientHospitals() != null ? patient.getPatientHospitals().stream().map(ph -> {
            PatientHistoryResponseDTO.HospitalDTO hdto = new PatientHistoryResponseDTO.HospitalDTO();
            hdto.setId(ph.getHospital().getId());
            hdto.setName(ph.getHospital().getName());
            hdto.setAddress(null); // No address mapping for now
            return hdto;
        }).toList() : java.util.Collections.emptyList());

        // LabResults: Not directly available, set as empty for now
        dto.setLabResults(java.util.Collections.emptyList());

        dto.setAppointments(dto.getAppointments() != null ? dto.getAppointments() : java.util.Collections.emptyList());
        dto.setAdmissions(dto.getAdmissions() != null ? dto.getAdmissions() : java.util.Collections.emptyList());
        dto.setEhrs(dto.getEhrs() != null ? dto.getEhrs() : java.util.Collections.emptyList());

        return dto;
    }
}
