package com.elec_business.controller;

import com.elec_business.data.TestDataLoader;
import com.elec_business.controller.dto.BookingRequestDto;
import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.entity.Booking;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.elec_business.config.TestcontainersConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
 class BookingControllerTest {

    @Autowired
    public TestDataLoader testDataLoader;

    @MockitoBean
    MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    List<User> users = new ArrayList<>();
    List<Booking> bookings = new ArrayList<>();
    List<ChargingStation> stations = new ArrayList<>();

    @BeforeEach
     void setUp() throws Exception {
        TestDataLoader.LoadResult result = testDataLoader.load();
        users = result.users();
        stations = result.stations();
        bookings = result.bookings();
        assertFalse(users.isEmpty(), "users should not be empty after loading test data");
        assertFalse(stations.isEmpty(), "stations should not be empty after loading test data");
        assertFalse(bookings.isEmpty(), "bookings should not be empty after loading test data");
    }

    @Test
    @WithMockUser(username = "user1", roles = {"ROLE_USER"})
    void createBooking_shouldCreateBookingSuccessfully() throws Exception {
        // Récupération de la station pour la réservation
        String stationId = stations.getFirst().getId();

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(3);

        BookingRequestDto requestDto = new BookingRequestDto(stationId, start, end);

        mvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    BookingResponseDto responseDto = objectMapper.readValue(content, BookingResponseDto.class);
                    assertNotNull(responseDto.getId(), "Booking ID should not be null");
                    assertFalse(responseDto.getStationName().isBlank(), "Station name should be present");
                    assertFalse(responseDto.getUserName().isBlank(), "User name should be present");
                    assertEquals("user2", responseDto.getUserName(), "Booking should be linked to user2");
                });
    }

    @Test
    @WithMockUser(username = "user1", roles = {"ROLE_USER"})
    void acceptBooking_shouldAcceptBookingSuccessfully() throws Exception {
        mvc.perform(post("/api/bookings/"+bookings.getFirst().getId()+"/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookings.getFirst().getId())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"ROLE_USER"})
    void rejectBooking_shouldRejectBookingSuccessfully() throws Exception {
        mvc.perform(post("/api/bookings/" + bookings.getFirst().getId() + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookings.getFirst().getId())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"ROLE_USER"})
    void getBooking_shouldReturnBookingSuccessfully() throws Exception {
        mvc.perform(get("/api/bookings/" + bookings.getFirst().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate")
                        .value(bookings.getFirst().getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))))
                .andExpect(jsonPath("$.endDate").value(bookings.getFirst().getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))))
                .andExpect(jsonPath("$.statusLabel").value(bookings.getFirst().getStatus().getName().name()))
                .andExpect(jsonPath("$.totalPrice").value(bookings.getFirst().getTotalPrice().doubleValue()))
                .andExpect(jsonPath("$.userName").value(users.getFirst().getUsername()))
                .andExpect(jsonPath("$.stationName").value(stations.getFirst().getName()))
                .andExpect(jsonPath("$.stationOwnerName").value(stations.getFirst().getLocation().getUser().getUsername()));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"ROLE_USER"})
    void putShouldUpdateBooking() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusHours(3);
        LocalDateTime end = start.plusHours(4);

        String stationId = stations.getFirst().getId();

        BookingRequestDto requestDto = new BookingRequestDto(stationId, start, end);

        mvc.perform(put("/api/bookings/" + bookings.getFirst().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isAccepted())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    BookingResponseDto responseDto = objectMapper.readValue(content, BookingResponseDto.class);
                    assertNotNull(responseDto.getId(), "Booking ID should not be null");
                    assertFalse(responseDto.getStationName().isBlank(), "Station name should be present");
                    assertFalse(responseDto.getUserName().isBlank(), "User name should be present");
                });
    }

    @Test
    @WithMockUser(username = "user1", roles = {"ROLE_USER"})
    void putShouldFailOnValidationError() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto(); // vide = validation error

        mvc.perform(put("/api/bookings/" + bookings.getFirst().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"ROLE_USER"})
    void putShouldThrow404IfNotExist() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        String stationId = stations.getFirst().getId();

        BookingRequestDto requestDto = new BookingRequestDto(stationId, start, end);

        mvc.perform(put("/api/bookings/dontexist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    // a voir si je supprime le booking ou si je met le statut annulé
    @Test
    @WithMockUser(username = "user1", roles = {"ROLE_USER"})
    void deleteShouldDeleteBookingSuccessfully() throws Exception {
        mvc.perform(delete("/api/bookings/" + bookings.getFirst().getId()))
                .andExpect(status().isNoContent());

        // Vérifier que le booking est bien supprimé
        mvc.perform(get("/api/bookings/" + bookings.getFirst().getId()))
                .andExpect(status().isNotFound());
    }

}
