package com.elec_business.controller;

import com.elec_business.config.TestSecurityConfig;
import com.elec_business.data.TestDataLoader;
import com.elec_business.controller.dto.BookingRequestDto;
import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.entity.Booking;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.User;
import com.elec_business.service.EmailService; // 1. IMPORT AJOUTÉ
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; // 2. IMPORT AJOUTÉ
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.elec_business.config.TestcontainersConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, TestSecurityConfig.class})
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingControllerTest {

    @Autowired
    private TestDataLoader testDataLoader;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 👇 3. C'EST LE FIX MAGIQUE 👇
    // Cela empêche Spring d'essayer d'envoyer un vrai email et de planter avec une erreur 500
    @MockBean
    private EmailService emailService;

    List<User> users = new ArrayList<>();
    List<Booking> bookings = new ArrayList<>();
    List<ChargingStation> stations = new ArrayList<>();

    @BeforeAll
    void setUp() throws Exception {
        TestDataLoader.LoadResult result = testDataLoader.load();

        users = result.users();
        stations = result.stations();
        bookings = result.bookings();

        assertFalse(users.isEmpty());
        assertFalse(stations.isEmpty());
        assertFalse(bookings.isEmpty());
    }

    // ... LE RESTE DE TES TESTS RESTE IDENTIQUE ...

    // ------------------------------------------
    // CREATE BOOKING
    // ------------------------------------------
    @Test
    @Order(1)
    @WithUserDetails(value = "user2@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createBooking_shouldCreateBookingSuccessfully() throws Exception {
        // ... (Ton code existant) ...
        String stationId = stations.getFirst().getId();
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(3);

        BookingRequestDto requestDto = new BookingRequestDto(stationId, start, end);

        mvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    BookingResponseDto dto =
                            objectMapper.readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);

                    assertNotNull(dto.getId());
                    assertEquals("user2", dto.getUserName());
                });
    }

    // ------------------------------------------
    // ACCEPT BOOKING - utilise booking index 0
    // ------------------------------------------
    @Test
    @Order(2)
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void acceptBooking_shouldAcceptBookingSuccessfully() throws Exception {
        Booking bookingToAccept = bookings.get(0);

        mvc.perform(post("/api/bookings/" + bookingToAccept.getId() + "/accept"))
                .andExpect(status().isOk());
    }

    // ------------------------------------------
    // REJECT BOOKING - utilise booking index 1
    // ------------------------------------------
    @Test
    @Order(3)
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void rejectBooking_shouldRejectBookingSuccessfully() throws Exception {
        Booking bookingToReject = bookings.get(1);

        mvc.perform(post("/api/bookings/" + bookingToReject.getId() + "/reject"))
                .andExpect(status().isOk());
    }

    // ------------------------------------------
    // GET BOOKING BY ID - utilise booking index 2
    // ------------------------------------------
    @Test
    @Order(4)
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getBooking_shouldReturnBookingSuccessfully() throws Exception {
        Booking bookingToGet = bookings.get(2);

        mvc.perform(get("/api/bookings/" + bookingToGet.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate")
                        .value(bookingToGet.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))));
    }

    // ------------------------------------------
    // UPDATE BOOKING - utilise booking index 3
    // ------------------------------------------
    @Test
    @Order(5)
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void putShouldUpdateBooking() throws Exception {
        Booking bookingToUpdate = bookings.get(3);

        LocalDateTime start = LocalDateTime.now().plusHours(3);
        LocalDateTime end = start.plusHours(4);

        BookingRequestDto requestDto =
                new BookingRequestDto(bookingToUpdate.getStation().getId(), start, end);

        mvc.perform(put("/api/bookings/" + bookingToUpdate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isAccepted())
                .andExpect(result -> {
                    BookingResponseDto dto =
                            objectMapper.readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);

                    assertNotNull(dto.getId());
                });
    }

    // ------------------------------------------
    // UPDATE BAD REQUEST
    // ------------------------------------------
    @Test
    @Transactional
    @Order(6)
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void putShouldFailOnValidationError() throws Exception {
        mvc.perform(put("/api/bookings/" + bookings.getFirst().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookingRequestDto())))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------
    // UPDATE NOT FOUND
    // ------------------------------------------
    @Test
    @Order(7)
    @Transactional
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void putShouldThrow404IfNotExist() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        String stationId = stations.getFirst().getId();

        BookingRequestDto requestDto = new BookingRequestDto(stationId, start, end);

        mvc.perform(put("/api/bookings/notexist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    // ------------------------------------------
    // DELETE BOOKING - utilise booking index 4
    // ------------------------------------------
    @Test
    @Order(8)
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteShouldDeleteBookingSuccessfully() throws Exception {
        Booking bookingToDelete = bookings.get(4);

        mvc.perform(delete("/api/bookings/" + bookingToDelete.getId()))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/bookings/" + bookingToDelete.getId()))
                .andExpect(status().isNotFound());
    }
}