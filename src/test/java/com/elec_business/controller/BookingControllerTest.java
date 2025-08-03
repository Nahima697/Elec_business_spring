package com.elec_business.controller;

import com.elec_business.TestDataLoader;
import com.elec_business.controller.dto.BookingRequestDto;
import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.entity.User;
import com.elec_business.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
 class BookingControllerTest {
    @Autowired
    public TestDataLoader testDataLoader;

    @Autowired
    MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    List<String> userIds = new ArrayList<>();
    List<String> bookingIds = new ArrayList<>();
    List<String> stationIds = new ArrayList<>();

    @BeforeEach
     void setUp() throws Exception {
        TestDataLoader.LoadResult result = testDataLoader.load();
        userIds = result.userIds();
        stationIds = result.stationsIds();
        bookingIds = result.bookingsIds();
        assertFalse(userIds.isEmpty(), "userIds should not be empty after loading test data");
        assertFalse(stationIds.isEmpty(), "stationIds should not be empty after loading test data");
        assertFalse(bookingIds.isEmpty(), "bookingIds should not be empty after loading test data");
    }

    @Test
    void createBooking_shouldCreateBookingSuccessFully() throws Exception {
        User mockUser = userRepository.findById(userIds.getFirst())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        BookingRequestDto requestDto = new BookingRequestDto(stationIds.getFirst(), Instant.now(),Instant.now().plusSeconds(7200));
        mvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    BookingResponseDto response = objectMapper.readValue(content, BookingResponseDto.class);
                    assertNotNull(response.getId(), "Booking ID should not be null");
                    assertFalse(response.getStationName().isBlank(), "Station name should be present");
                    assertFalse(response.getUserName().isBlank(), "User name should be present");
                });
    }
}
