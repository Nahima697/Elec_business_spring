package com.elec_business.business.impl;

import com.elec_business.controller.dto.ChargingLocationRequestDto;
import com.elec_business.controller.dto.ChargingLocationResponseDto;
import com.elec_business.controller.mapper.ChargingLocationMapper;
import com.elec_business.entity.ChargingLocation;
import com.elec_business.entity.User;
import com.elec_business.repository.ChargingLocationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargingLocationBusinessTest {

    @Mock
    private ChargingLocationRepository chargingLocationRepository;

    @Mock
    private ChargingLocationMapper chargingLocationMapper;

    @InjectMocks
    private ChargingLocationBusinessImpl chargingLocationBusiness;

    @Test
    void createChargingLocation_Success() {

        ChargingLocation location = new ChargingLocation();
        ChargingLocationResponseDto dto =
                mock(ChargingLocationResponseDto.class);

        when(chargingLocationRepository.save(location))
                .thenReturn(location);

        when(chargingLocationMapper.toDto(location))
                .thenReturn(dto);

        ChargingLocationResponseDto result =
                chargingLocationBusiness.createChargingLocation(location);

        assertNotNull(result);
        verify(chargingLocationRepository).save(location);
        verify(chargingLocationMapper).toDto(location);
    }

    @Test
    void getChargingLocationById_Success() throws Exception {

        User owner = new User();
        owner.setId("u1");

        ChargingLocation location = new ChargingLocation();
        location.setId("loc1");
        location.setUser(owner);

        ChargingLocationResponseDto dto =
                mock(ChargingLocationResponseDto.class);

        when(chargingLocationRepository.findById("loc1"))
                .thenReturn(Optional.of(location));

        when(chargingLocationMapper.toDto(location))
                .thenReturn(dto);

        ChargingLocationResponseDto result =
                chargingLocationBusiness.getChargingLocationById("loc1", owner);

        assertNotNull(result);
        verify(chargingLocationMapper).toDto(location);
    }

    @Test
    void getChargingLocationById_Fail_AccessDenied() {

        User owner = new User(); owner.setId("u1");
        User hacker = new User(); hacker.setId("hack");

        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);

        when(chargingLocationRepository.findById("loc1"))
                .thenReturn(Optional.of(location));

        assertThrows(AccessDeniedException.class, () ->
                chargingLocationBusiness.getChargingLocationById("loc1", hacker)
        );
    }

    @Test
    void updateChargingLocation_Success() throws Exception {

        User owner = new User(); owner.setId("u1");

        ChargingLocation existing = new ChargingLocation();
        existing.setId("loc1");
        existing.setUser(owner);

        ChargingLocationRequestDto requestDto =
                mock(ChargingLocationRequestDto.class);

        ChargingLocation mappedLocation =
                new ChargingLocation();
        mappedLocation.setName("New Name");

        ChargingLocationResponseDto responseDto =
                mock(ChargingLocationResponseDto.class);

        when(chargingLocationRepository.findById("loc1"))
                .thenReturn(Optional.of(existing));

        when(chargingLocationMapper.toEntity(requestDto))
                .thenReturn(mappedLocation);

        when(chargingLocationRepository.save(existing))
                .thenReturn(existing);

        when(chargingLocationMapper.toDto(existing))
                .thenReturn(responseDto);

        ChargingLocationResponseDto result =
                chargingLocationBusiness.updateChargingLocation(
                        "loc1",
                        requestDto,
                        owner
                );

        assertNotNull(result);
        verify(chargingLocationRepository).save(existing);
        verify(chargingLocationMapper).toDto(existing);
    }

    @Test
    void deleteChargingLocation_Success() throws Exception {

        User owner = new User(); owner.setId("u1");

        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);

        ChargingLocationResponseDto dto =
                mock(ChargingLocationResponseDto.class);

        when(chargingLocationRepository.findById("loc1"))
                .thenReturn(Optional.of(location));

        when(chargingLocationMapper.toDto(location))
                .thenReturn(dto);

        when(chargingLocationMapper.toEntity(dto))
                .thenReturn(location);

        chargingLocationBusiness.deleteChargingLocation("loc1", owner);

        verify(chargingLocationRepository).delete(location);
    }
}
