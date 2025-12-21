package com.elec_business.controller;

import com.elec_business.config.TestSecurityConfig;
import com.elec_business.data.TestDataLoader;
import com.elec_business.controller.dto.BookingRequestDto;
import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.entity.Booking;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.elec_business.config.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
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

    //  UTILITAIRE : Récupère un booking dont la STATION appartient à cet email (pour accept/reject)
    private Booking getBookingForStationOwnedBy(String email) {
        return bookings.stream()
                .filter(b -> b.getStation().getLocation().getUser().getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucun booking pour station de " + email));
    }

    //  UTILITAIRE : Récupère un booking CRÉÉ par cet utilisateur (pour update/delete)
    private Booking getBookingCreatedBy(String email) {
        return bookings.stream()
                .filter(b -> b.getUser().getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucun booking créé par " + email));
    }

    // ------------------------------------------
    // CREATE BOOKING
    // ------------------------------------------
    @Test
    @Order(1)
    @WithUserDetails(value = "user2@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createBooking_shouldCreateBookingSuccessfully() throws Exception {

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
    // ACCEPT BOOKING
    // ------------------------------------------
    @Test
    @Order(2)
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void acceptBooking_shouldAcceptBookingSuccessfully() throws Exception {
        // user1 accepte un booking fait sur SA station
        Booking bookingForHisStation = getBookingForStationOwnedBy("user1@test.com");

        mvc.perform(post("/api/bookings/" + bookingForHisStation.getId() + "/accept"))
                .andExpect(status().isOk());
    }

    // ------------------------------------------
    // REJECT BOOKING
    // ------------------------------------------
    @Test
    @Order(3)
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void rejectBooking_shouldRejectBookingSuccessfully() throws Exception {
        // user1 rejette un booking fait sur SA station
        Booking bookingForHisStation = getBookingForStationOwnedBy("user1@test.com");

        mvc.perform(post("/api/bookings/" + bookingForHisStation.getId() + "/reject"))
                .andExpect(status().isOk());
    }

    // ------------------------------------------
    // GET BOOKING BY ID
    // ------------------------------------------
    @Test
    @Order(4)
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getBooking_shouldReturnBookingSuccessfully() throws Exception {

        Booking booking = bookings.stream()
                .filter(b -> b.getUser().getEmail().equals("user1@test.com") ||
                        b.getStation().getLocation().getUser().getEmail().equals("user1@test.com"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucune réservation accessible pour user1"));

        mvc.perform(get("/api/bookings/" + booking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate")
                        .value(booking.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))));
    }
    // ------------------------------------------
    // UPDATE BOOKING
    // ------------------------------------------
    @Test
    @Order(5)
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void putShouldUpdateBooking() throws Exception {
        // user1 modifie SON propre booking
        Booking ownBooking = getBookingCreatedBy("user1@test.com");

        LocalDateTime start = LocalDateTime.now().plusHours(3);
        LocalDateTime end = start.plusHours(4);

        BookingRequestDto requestDto =
                new BookingRequestDto(ownBooking.getStation().getId(), start, end);

        mvc.perform(put("/api/bookings/" + ownBooking.getId())
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
    // DELETE BOOKING
    // ------------------------------------------
    @Test
    @Order(8)
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteShouldDeleteBookingSuccessfully() throws Exception {
        // user1 supprime SON propre booking
        Booking ownBooking = getBookingCreatedBy("user1@test.com");

        mvc.perform(delete("/api/bookings/" + ownBooking.getId()))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/bookings/" + ownBooking.getId()))
                .andExpect(status().isNotFound());
    }
}