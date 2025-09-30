package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);

    boolean existsByPhone(String phone);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.hospital.id = :hospitalId AND u.role = :role AND u.isActive = true")
    boolean existsByHospitalIdAndRole(@Param("hospitalId") Long hospitalId, @Param("role") User.UserRole role);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    Optional<User> findActiveUserByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isActive = true")
    Optional<User> findActiveUserById(@Param("id") Long id);

    @Query("SELECT h.id FROM Hospital h WHERE h.isActive = true AND NOT EXISTS (SELECT u FROM User u WHERE u.hospital.id = h.id AND u.role = 'HOSPITAL_ADMIN' AND u.isActive = true)")
    List<Long> findAvailableHospitalIds();

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.id != :excludeId AND u.isActive = true")
    boolean existsByEmailExcludingId(@Param("email") String email, @Param("excludeId") Long excludeId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.nationalId = :nationalId AND u.id != :excludeId AND u.isActive = true")
    boolean existsByNationalIdExcludingId(@Param("nationalId") String nationalId, @Param("excludeId") Long excludeId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.phone = :phone AND u.id != :excludeId AND u.isActive = true")
    boolean existsByPhoneExcludingId(@Param("phone") String phone, @Param("excludeId") Long excludeId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.hospital WHERE u.role = 'HOSPITAL_ADMIN' AND u.isActive = true " +
            "AND (:hospitalId IS NULL OR u.hospital.id = :hospitalId)")
    Page<User> findHospitalAdmins(@Param("hospitalId") Long hospitalId, Pageable pageable);
}
