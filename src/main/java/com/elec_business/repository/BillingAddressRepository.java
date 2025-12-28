package com.elec_business.repository;

import com.elec_business.entity.BillingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingAddressRepository extends JpaRepository<BillingAddress,String> {
    BillingAddress findByUserId(String userId);
}
