package com.elec_business.controller;

import com.elec_business.TestDataLoader;
import com.elec_business.controller.dto.BookingRequestDto;
import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.entity.Booking;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.User;
import com.elec_business.repository.TimeSlotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
 class BookingControllerTest {
    @Autowired
    public TestDataLoader testDataLoader;

    @Autowired
    MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    TimeSlotRepository timeSlotRepository;

    List<User> users = new ArrayList<>();
    List<Booking> bookings = new ArrayList<>();
    List<ChargingStation> stations = new ArrayList<>();
    String jwtTokenUser1;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:17-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() throws Exception {
        // Charge les données de test
        TestDataLoader.LoadResult result = testDataLoader.load();
        users = result.users();
        stations = result.stations();
        bookings = result.bookings();

        // Login programmatique pour user1
        jwtTokenUser1 = loginAndGetToken("user1@test.com", "password123");
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String payload = """
            {
                "username": "%s",
                "password": "%s"
            }
            """.formatted(username, password);

        String response = mvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }


    @Test
    void createBooking_shouldCreateBookingSuccessFully() throws Exception {
        String username = "user2";
        String password = "password456";

        String loginPayload = """
        {
            "username": "%s",
            "password": "%s"
        }
        """.formatted(username, password);

        String response = mvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwtToken = objectMapper.readTree(response).get("token").asText();
        assertNotNull(jwtToken, "Le token ne doit pas être null");

        LocalDateTime start = LocalDateTime.of(2025, 9, 10, 8, 20);
        LocalDateTime end = LocalDateTime.of(2025, 9, 10, 10, 20);


        BookingRequestDto requestDto = new BookingRequestDto(stations.getFirst().getId(), start, end);

        mvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    BookingResponseDto responseDto = objectMapper.readValue(content, BookingResponseDto.class);
                    assertNotNull(responseDto.getId(), "Booking ID should not be null");
                    assertFalse(responseDto.getStationName().isBlank(), "Station name should be present");
                    assertFalse(responseDto.getUserName().isBlank(), "User name should be present");
                });
    }

    @Test
    void acceptBooking_shouldAcceptBookingSuccessfully() throws Exception {
        mvc.perform(post("/api/bookings/"+bookings.getFirst().getId()+"/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookings.getFirst().getId())))
                .andExpect(status().isOk());
    }

    @Test
    void rejectBooking_shouldRejectBookingSuccessfully() throws Exception {
        mvc.perform(post("/api/bookings/" + bookings.getFirst().getId() + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookings.getFirst().getId())))
                .andExpect(status().isOk());
    }

    @Test
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
    void putShouldUpdateBooking() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 9, 10, 14, 20);
        LocalDateTime end = LocalDateTime.of(2025, 9, 10, 15, 20);
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
    void putShouldFailOnValidationError() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto(); // vide = validation error

        mvc.perform(put("/api/bookings/" + bookings.getFirst().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putShouldThrow404IfNotExist() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 9, 10, 14, 20);
        LocalDateTime end = LocalDateTime.of(2025, 9, 10, 15, 20);
        String stationId = stations.getFirst().getId();

        BookingRequestDto requestDto = new BookingRequestDto(stationId, start, end);

        mvc.perform(put("/api/bookings/dontexist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldDeleteBookingSuccessfully() throws Exception {
        mvc.perform(delete("/api/bookings/" + bookings.getFirst().getId()))
                .andExpect(status().isNoContent());

        // Vérifier que le booking est bien supprimé
        mvc.perform(get("/api/bookings/" + bookings.getFirst().getId()))
                .andExpect(status().isNotFound());
    }

}
