package com.elec_business.repository;

import com.elec_business.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingStatusRepository extends JpaRepository {
  Optional<BookingStatus> findByName(String name);
}
