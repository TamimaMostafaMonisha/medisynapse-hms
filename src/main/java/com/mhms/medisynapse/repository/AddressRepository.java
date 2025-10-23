package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Address entity.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}

