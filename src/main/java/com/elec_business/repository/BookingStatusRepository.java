package com.elec_business.repository;

import com.elec_business.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingStatusRepository extends JpaRepository<BookingStatus, Integer> {
  Optional<BookingStatus> findByName(String name);
}
