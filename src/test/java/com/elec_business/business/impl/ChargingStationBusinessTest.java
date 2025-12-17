package com.elec_business.business.impl;

import com.elec_business.entity.ChargingLocation;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.User;
import com.elec_business.repository.ChargingLocationRepository;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargingStationBusinessTest {

    @Mock
    private ChargingStationRepository chargingStationRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ChargingLocationRepository chargingLocationRepository;

    @InjectMocks
    private ChargingStationBusinessImpl chargingStationBusiness;

    @Test
    void createChargingStation_Success_WithImage() throws AccessDeniedException {
        // ARRANGE
        User user = new User();
        user.setId("user-123");

        ChargingLocation location = new ChargingLocation();
        location.setId("loc-123");
        location.setUser(user);

        ChargingStation station = new ChargingStation();
        station.setLocation(location);

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "content".getBytes());

        when(chargingLocationRepository.findById("loc-123")).thenReturn(Optional.of(location));
        when(fileStorageService.checkMediaType(image, "image")).thenReturn(true);
        when(fileStorageService.upload(image)).thenReturn("uploads/test.jpg");
        when(chargingStationRepository.save(any(ChargingStation.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        ChargingStation created = chargingStationBusiness.createChargingStation(station, user, image);

        // ASSERT
        assertNotNull(created);
        assertEquals("uploads/test.jpg", created.getImageUrl());
        assertNotNull(created.getCreatedAt());
        verify(chargingStationRepository).save(station);
    }

    @Test
    void createChargingStation_Success_NoImage() throws AccessDeniedException {
        // ARRANGE
        User user = new User();
        user.setId("user-123");
        ChargingLocation location = new ChargingLocation();
        location.setId("loc-123");
        location.setUser(user);
        ChargingStation station = new ChargingStation();
        station.setLocation(location);

        when(chargingLocationRepository.findById("loc-123")).thenReturn(Optional.of(location));
        when(chargingStationRepository.save(any(ChargingStation.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT (Image null)
        ChargingStation created = chargingStationBusiness.createChargingStation(station, user, null);

        // ASSERT
        assertEquals("default.png", created.getImageUrl());
    }

    @Test
    void createChargingStation_Throws_WhenNotOwner() {
        // ARRANGE
        User owner = new User(); owner.setId("owner-1");
        User hacker = new User(); hacker.setId("hacker-99");

        ChargingLocation location = new ChargingLocation();
        location.setId("loc-123");
        location.setUser(owner); 

        ChargingStation station = new ChargingStation();
        station.setLocation(location);

        when(chargingLocationRepository.findById("loc-123")).thenReturn(Optional.of(location));

        // ACT & ASSERT
        assertThrows(AccessDeniedException.class, () -> 
            chargingStationBusiness.createChargingStation(station, hacker, null)
        );
        verify(chargingStationRepository, never()).save(any());
    }

    @Test
    void updateChargingStation_Success() throws AccessDeniedException {
        // ARRANGE
        String stationId = "station-1";
        User user = new User(); user.setId("u1");
        
        ChargingLocation location = new ChargingLocation();
        location.setUser(user);

        ChargingStation existingStation = new ChargingStation();
        existingStation.setId(stationId);
        existingStation.setLocation(location);
        existingStation.setPrice(BigDecimal.TEN);

        ChargingStation updateInfo = new ChargingStation();
        updateInfo.setName("New Name");
        updateInfo.setPrice(BigDecimal.ONE);

        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(existingStation));
        when(chargingStationRepository.save(any(ChargingStation.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        ChargingStation updated = chargingStationBusiness.updateChargingStation(stationId, updateInfo, user);

        // ASSERT
        assertEquals("New Name", updated.getName());
        assertEquals(BigDecimal.ONE, updated.getPrice());
    }

    @Test
    void deleteChargingStation_Success() throws AccessDeniedException {
        // ARRANGE
        String stationId = "station-1";
        User user = new User(); user.setId("u1");
        ChargingLocation location = new ChargingLocation();
        location.setUser(user);
        ChargingStation station = new ChargingStation();
        station.setLocation(location);

        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(station));

        // ACT
        chargingStationBusiness.deleteChargingStationById(stationId, user);

        // ASSERT
        verify(chargingStationRepository).delete(station);
    }
}
