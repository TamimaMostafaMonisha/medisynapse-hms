package com.mhms.medisynapse.repository;

import com.mhms.medisynapse.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    @Query("SELECT bi FROM BillItem bi WHERE bi.billing.id = :billingId AND bi.isActive = true")
    List<BillItem> findByBillingId(@Param("billingId") Long billingId);

    @Query("SELECT bi FROM BillItem bi WHERE bi.billing.id = :billingId AND bi.serviceType = :serviceType AND bi.isActive = true")
    List<BillItem> findByBillingIdAndServiceType(@Param("billingId") Long billingId, @Param("serviceType") String serviceType);
}

