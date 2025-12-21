package com.elec_business.business.impl;

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

    @InjectMocks
    private ChargingLocationBusinessImpl chargingLocationBusiness;

    @Test
    void createChargingLocation_Success() {
        ChargingLocation loc = new ChargingLocation();
        when(chargingLocationRepository.save(loc)).thenReturn(loc);

        ChargingLocation result = chargingLocationBusiness.createChargingLocation(loc);
        assertNotNull(result);
    }

    @Test
    void getChargingLocationById_Success() throws AccessDeniedException {
        // ARRANGE
        User owner = new User(); owner.setId("u1");
        ChargingLocation loc = new ChargingLocation();
        loc.setId("loc1");
        loc.setUser(owner);

        when(chargingLocationRepository.findById("loc1")).thenReturn(Optional.of(loc));

        // ACT
        ChargingLocation result = chargingLocationBusiness.getChargingLocationById("loc1", owner);

        // ASSERT
        assertEquals("loc1", result.getId());
    }

    @Test
    void getChargingLocationById_Fail_AccessDenied() {
        // ARRANGE : Le user connecté n'est PAS le propriétaire
        User owner = new User(); owner.setId("u1");
        User hacker = new User(); hacker.setId("hacker");

        ChargingLocation loc = new ChargingLocation();
        loc.setUser(owner);

        when(chargingLocationRepository.findById("loc1")).thenReturn(Optional.of(loc));

        // ACT & ASSERT
        assertThrows(AccessDeniedException.class, () ->
                chargingLocationBusiness.getChargingLocationById("loc1", hacker)
        );
    }

    @Test
    void updateChargingLocation_Success() throws AccessDeniedException {
        User owner = new User(); owner.setId("u1");
        ChargingLocation existing = new ChargingLocation();
        existing.setId("loc1");
        existing.setUser(owner);
        existing.setName("Old Name");

        ChargingLocation updateData = new ChargingLocation();
        updateData.setName("New Name");

        when(chargingLocationRepository.findById("loc1")).thenReturn(Optional.of(existing));
        when(chargingLocationRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        ChargingLocation updated = chargingLocationBusiness.updateChargingLocation("loc1", updateData, owner);

        assertEquals("New Name", updated.getName());
    }

    @Test
    void deleteChargingLocation_Success() throws AccessDeniedException {
        User owner = new User(); owner.setId("u1");
        ChargingLocation loc = new ChargingLocation();
        loc.setUser(owner);

        when(chargingLocationRepository.findById("loc1")).thenReturn(Optional.of(loc));

        chargingLocationBusiness.deleteChargingLocation("loc1", owner);

        verify(chargingLocationRepository).delete(loc);
    }
}