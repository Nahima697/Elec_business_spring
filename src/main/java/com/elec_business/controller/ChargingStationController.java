package com.elec_business.controller;

import com.elec_business.business.ChargingStationBusiness;
import com.elec_business.controller.dto.ChargingStationRequestDto;
import com.elec_business.controller.dto.ChargingStationResponseDto;
import com.elec_business.controller.dto.ChargingStationUpdateRequestDto;
import com.elec_business.controller.mapper.ChargingStationMapper;
import com.elec_business.entity.User;
import com.elec_business.entity.ChargingStation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Charging Stations", description = "API de gestion des bornes de recharge")
public class ChargingStationController {

    private final ChargingStationBusiness chargingStationBusiness;
    private final ChargingStationMapper chargingStationMapper;

    @Operation(
            summary = "Créer une nouvelle borne de recharge",
            description = "Permet à un propriétaire de créer une borne de recharge avec une image. Nécessite le rôle OWNER."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Borne créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChargingStationResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides (ex: image trop volumineuse, champs manquants)"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès refusé - vous n'êtes pas le propriétaire de cet emplacement"
            )
    })

    @PostMapping(value = "/charging_stations",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ChargingStationResponseDto addChargingStation(@Valid   @ModelAttribute ChargingStationRequestDto dto,
                                                         @AuthenticationPrincipal User currentUser, MultipartFile image) throws AccessDeniedException {

       return chargingStationMapper.toDto(chargingStationBusiness.createChargingStation(chargingStationMapper.toEntity(dto), currentUser,image));

    }

    @PutMapping("/charging_stations/{id}")
    public ChargingStationResponseDto updateStation(@PathVariable String id,
                                                    @RequestBody @Valid ChargingStationUpdateRequestDto dto,@AuthenticationPrincipal User currentUser) throws AccessDeniedException {

        return chargingStationMapper.toDto(chargingStationBusiness.updateChargingStation(id, chargingStationMapper.toUpdateEntity(dto), currentUser));
    }

    @GetMapping("/charging_stations")
    public Page<ChargingStationResponseDto> getAllChargingStations(Pageable pageable) {
        return chargingStationBusiness.getAllChargingStations(pageable).map(chargingStationMapper::toDto);
    };

    @GetMapping("/charging_stations/location/{locationId}")
    public List<ChargingStationResponseDto> getChargingStationsByUser(@PathVariable String locationId) {
        return chargingStationBusiness.getByLocationId(locationId)
                .stream()
                .map(chargingStationMapper::toDto)
                .toList();
    }

    @GetMapping("/charging_stations/{id}")
    public ChargingStationResponseDto getStation(@PathVariable String id) {
        ChargingStation station = chargingStationBusiness.getChargingStationById(id);
        return chargingStationMapper.toDto(station);
    }

    @DeleteMapping("/charging_stations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChargingStation(@PathVariable String id,
                                      @AuthenticationPrincipal User currentUser) throws AccessDeniedException {
        chargingStationBusiness.deleteChargingStationById(id, currentUser);
    }

}
