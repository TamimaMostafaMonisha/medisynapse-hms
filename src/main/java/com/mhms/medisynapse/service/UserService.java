package com.mhms.medisynapse.service;

import com.mhms.medisynapse.dto.AvailableHospitalDto;
import com.mhms.medisynapse.dto.CreateHospitalAdminDto;
import com.mhms.medisynapse.dto.CreateReceptionistDto;
import com.mhms.medisynapse.dto.DoctorAvailabilityDto;
import com.mhms.medisynapse.dto.DoctorListResponseDto;
import com.mhms.medisynapse.dto.HospitalAdminPagedResponseDto;
import com.mhms.medisynapse.dto.HospitalAdminResponseDto;
import com.mhms.medisynapse.dto.PasswordResetResponseDto;
import com.mhms.medisynapse.dto.ReceptionistPagedResponseDto;
import com.mhms.medisynapse.dto.ReceptionistResponseDto;
import com.mhms.medisynapse.dto.ResetPasswordDto;
import com.mhms.medisynapse.dto.UpdateHospitalAdminDto;
import com.mhms.medisynapse.dto.UpdateReceptionistDto;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    HospitalAdminResponseDto createHospitalAdmin(CreateHospitalAdminDto createHospitalAdminDto);

    HospitalAdminResponseDto updateHospitalAdmin(Long id, UpdateHospitalAdminDto updateHospitalAdminDto);

    PasswordResetResponseDto resetHospitalAdminPassword(Long id, ResetPasswordDto resetPasswordDto);

    List<AvailableHospitalDto> getAvailableHospitals();

    HospitalAdminPagedResponseDto getHospitalAdmins(int page, int size, String sortBy, String sortDir, Long hospitalId);

    // Doctor management methods
    DoctorListResponseDto getDoctorsByHospital(Long hospitalId);

    DoctorAvailabilityDto getDoctorAvailability(Long doctorId, LocalDate date);

    // Receptionist management
    ReceptionistResponseDto createReceptionist(CreateReceptionistDto dto);
    ReceptionistResponseDto updateReceptionist(Long id, UpdateReceptionistDto dto);
    PasswordResetResponseDto resetReceptionistPassword(Long id, ResetPasswordDto resetPasswordDto);
    ReceptionistPagedResponseDto getReceptionists(int page, int size, String sortBy, String sortDir, Long hospitalId);
    ReceptionistResponseDto getReceptionistById(Long id);
    void deleteReceptionist(Long id);
}
