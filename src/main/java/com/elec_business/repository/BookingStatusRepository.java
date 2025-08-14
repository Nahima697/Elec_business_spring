package com.elec_business.repository;

import com.elec_business.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.elec_business.entity.BookingStatusType;

public interface BookingStatusRepository extends JpaRepository<BookingStatus, Integer> {
  Optional<BookingStatus> findByName(BookingStatusType name);
}
