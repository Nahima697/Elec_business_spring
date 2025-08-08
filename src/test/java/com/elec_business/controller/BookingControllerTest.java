package com.elec_business.controller;

import com.elec_business.TestDataLoader;
import com.elec_business.controller.dto.BookingRequestDto;
import com.elec_business.controller.dto.BookingResponseDto;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    List<String> userIds = new ArrayList<>();
    List<String> bookingIds = new ArrayList<>();
    List<String> stationIds = new ArrayList<>();


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
        TestDataLoader.LoadResult result = testDataLoader.load();
        userIds = result.userIds();
        stationIds = result.stationsIds();
        bookingIds = result.bookingsIds();
        assertFalse(userIds.isEmpty(), "userIds should not be empty after loading test data");
        assertFalse(stationIds.isEmpty(), "stationIds should not be empty after loading test data");
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


        BookingRequestDto requestDto = new BookingRequestDto(stationIds.getFirst(), start, end);

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
    @WithUserDetails(value = "user1@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void acceptBooking_shouldAcceptBookingSuccessfully() throws Exception {
        mvc.perform(post("/api/bookings/"+bookingIds.getFirst()+"/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingIds.getFirst())))
                .andExpect(status().isOk());
    }

}
