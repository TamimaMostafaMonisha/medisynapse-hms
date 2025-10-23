package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.dto.AddressDto;
import com.mhms.medisynapse.dto.CreatePatientRequestDto;
import com.mhms.medisynapse.dto.CreatePatientResponseDto;
import com.mhms.medisynapse.dto.EmergencyContactDto;
import com.mhms.medisynapse.dto.PaginationDto;
import com.mhms.medisynapse.dto.PatientListDto;
import com.mhms.medisynapse.dto.PatientPagedResponseDto;
import com.mhms.medisynapse.dto.UpdatePatientRequestDto;
import com.mhms.medisynapse.dto.UpdatePatientResponseDto;
import com.mhms.medisynapse.entity.Address;
import com.mhms.medisynapse.entity.Admission;
import com.mhms.medisynapse.entity.Hospital;
import com.mhms.medisynapse.entity.Patient;
import com.mhms.medisynapse.entity.PatientHospital;
import com.mhms.medisynapse.repository.AdmissionRepository;
import com.mhms.medisynapse.repository.AppointmentRepository;
import com.mhms.medisynapse.repository.HospitalRepository;
import com.mhms.medisynapse.repository.PatientRepository;
import com.mhms.medisynapse.repository.AddressRepository;
import com.mhms.medisynapse.repository.PatientHospitalRepository;
import com.mhms.medisynapse.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
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

    private PatientListDto convertToPatientListDto(Patient patient) {
        // Calculate age from date of birth
        Integer age = null;
        if (patient.getDob() != null) {
            age = Period.between(patient.getDob(), LocalDate.now()).getYears();
        }

        // Get current admission information if patient is admitted
        String roomNumber = null;
        String admissionStatus = "Outpatient";
        Long assignedDoctorId = null;
        String assignedDoctorName = null;

        // Defensive copy of patient hospitals to avoid ConcurrentModificationException
        List<PatientHospital> patientHospitals = new ArrayList<>();
        if (patient.getPatientHospitals() != null) {
            patientHospitals.addAll(patient.getPatientHospitals());
        }
        Long hospitalId = null;
        if (!patientHospitals.isEmpty() && patientHospitals.get(0).getHospital() != null) {
            hospitalId = patientHospitals.get(0).getHospital().getId();
        }

        List<Admission> admissions = new ArrayList<>();
        if (hospitalId != null) {
            admissions.addAll(admissionRepository.findRecentAdmissionsByHospitalId(
                hospitalId,
                java.time.LocalDateTime.now().minusDays(30)
            ));
        }

        for (Admission admission : admissions) {
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

        // Create emergency contact from actual patient data
        EmergencyContactDto emergencyContact = EmergencyContactDto.builder()
                .name(patient.getEmergencyContactName())
                .relation(patient.getEmergencyContactRelation())
                .phone(patient.getEmergencyContactPhone())
                .build();

        // Extract address as a string
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

        return PatientListDto.builder()
                .id(patient.getId())
                .name(patient.getFirstName() + " " + patient.getLastName())
                .age(age)
                .gender(capitalizeFirstLetter(patient.getGender().toString()))
                .phone(patient.getContact())
                .email(patient.getEmail()) // Use actual email from database
                .address(addressString)
                .bloodGroup(patient.getBloodGroup()) // Use actual blood group from database
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

    private String generateMockEmail(String firstName, String lastName) {
        if (firstName != null && lastName != null) {
            return firstName.toLowerCase() + "." + lastName.toLowerCase() + "@email.com";
        }
        return null;
    }

    private String generateMockBloodGroup() {
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        return bloodGroups[(int) (Math.random() * bloodGroups.length)];
    }

    @Override
    @Transactional
    public CreatePatientResponseDto createPatient(CreatePatientRequestDto request) {
        log.info("Creating new patient: {}", request.getName());

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
}
