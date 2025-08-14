//package com.elec_business.config;
//
//import com.elec_business.entity.BookingStatus;
//import com.elec_business.entity.BookingStatusType;
//import com.elec_business.repository.BookingStatusRepository;
//import jakarta.annotation.PostConstruct;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class BookingStatusSeeder {
//
//    private final BookingStatusRepository bookingStatusRepository;
//
//    @PostConstruct
//    @Transactional
//    public void seedStatuses() {
//        for (BookingStatusType type : BookingStatusType.values()) {
//            boolean exists = bookingStatusRepository.findByName(type).isPresent();
//            if (!exists) {
//                bookingStatusRepository.save(new BookingStatus(type));
//            }
//        }
//    }
//}
