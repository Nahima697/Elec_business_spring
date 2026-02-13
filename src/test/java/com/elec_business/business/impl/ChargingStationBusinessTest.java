package com.elec_business.business.impl;

import com.elec_business.controller.dto.ChargingStationResponseDto;
import com.elec_business.controller.mapper.ChargingStationMapper;
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

    @Mock
    private ChargingStationMapper chargingStationMapper;

    @InjectMocks
    private ChargingStationBusinessImpl chargingStationBusiness;

    @Test
    void createChargingStation_Success_WithImage() throws AccessDeniedException {

        User user = new User();
        user.setId("user-123");

        ChargingLocation location = new ChargingLocation();
        location.setId("loc-123");
        location.setUser(user);

        ChargingStation station = new ChargingStation();
        station.setLocation(location);

        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "content".getBytes());

        ChargingStationResponseDto dto =
                new ChargingStationResponseDto(
                        "station-1",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "uploads/test.jpg",
                        null,
                        null
                );

        when(chargingLocationRepository.findById("loc-123"))
                .thenReturn(Optional.of(location));

        when(fileStorageService.checkMediaType(image, "image"))
                .thenReturn(true);

        when(fileStorageService.upload(image))
                .thenReturn("uploads/test.jpg");

        when(chargingStationRepository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        when(chargingStationMapper.toDto(any()))
                .thenReturn(dto);

        ChargingStationResponseDto result =
                chargingStationBusiness.createChargingStation(station, user, image);

        assertNotNull(result);
        assertEquals("uploads/test.jpg", result.imageUrl());

        verify(chargingStationRepository).save(any());
    }

    @Test
    void createChargingStation_Success_NoImage() throws AccessDeniedException {

        User user = new User();
        user.setId("user-123");

        ChargingLocation location = new ChargingLocation();
        location.setId("loc-123");
        location.setUser(user);

        ChargingStation station = new ChargingStation();
        station.setLocation(location);

        ChargingStationResponseDto dto =
                new ChargingStationResponseDto(
                        "station-1",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "default.png",
                        null,
                        null
                );

        when(chargingLocationRepository.findById("loc-123"))
                .thenReturn(Optional.of(location));

        when(chargingStationRepository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        when(chargingStationMapper.toDto(any()))
                .thenReturn(dto);

        ChargingStationResponseDto result =
                chargingStationBusiness.createChargingStation(station, user, null);

        assertEquals("default.png", result.imageUrl());
    }

    @Test
    void createChargingStation_Throws_WhenNotOwner() {

        User owner = new User(); owner.setId("owner-1");
        User hacker = new User(); hacker.setId("hacker-99");

        ChargingLocation location = new ChargingLocation();
        location.setId("loc-123");
        location.setUser(owner);

        ChargingStation station = new ChargingStation();
        station.setLocation(location);

        when(chargingLocationRepository.findById("loc-123"))
                .thenReturn(Optional.of(location));

        assertThrows(AccessDeniedException.class, () ->
                chargingStationBusiness.createChargingStation(station, hacker, null)
        );

        verify(chargingStationRepository, never()).save(any());
    }

    @Test
    void updateChargingStation_Success_NoImage() throws AccessDeniedException {

        String stationId = "station-1";
        User user = new User(); user.setId("u1");

        ChargingLocation location = new ChargingLocation();
        location.setUser(user);

        ChargingStation existingStation = new ChargingStation();
        existingStation.setId(stationId);
        existingStation.setLocation(location);
        existingStation.setImageUrl("old-image.jpg");

        ChargingStation updateInfo = new ChargingStation();
        updateInfo.setName("New Name");

        ChargingStationResponseDto dto =
                new ChargingStationResponseDto(
                        stationId,
                        "New Name",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "old-image.jpg",
                        null,
                        null
                );

        when(chargingStationRepository.findById(stationId))
                .thenReturn(Optional.of(existingStation));

        when(chargingStationRepository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        when(chargingStationMapper.toDto(any()))
                .thenReturn(dto);

        ChargingStationResponseDto result =
                chargingStationBusiness.updateChargingStation(stationId, updateInfo, user, null);

        assertEquals("New Name", result.name());
        assertEquals("old-image.jpg", result.imageUrl());
    }

    @Test
    void deleteChargingStation_Success() throws AccessDeniedException {

        String stationId = "station-1";
        User user = new User(); user.setId("u1");

        ChargingLocation location = new ChargingLocation();
        location.setUser(user);

        ChargingStation station = new ChargingStation();
        station.setLocation(location);

        when(chargingStationRepository.findById(stationId))
                .thenReturn(Optional.of(station));

        chargingStationBusiness.deleteChargingStationById(stationId, user);

        verify(chargingStationRepository).delete(station);
    }
}
