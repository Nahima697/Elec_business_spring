package com.elec_business.controller;

import com.elec_business.business.ChargingStationBusiness;
import com.elec_business.controller.dto.ChargingStationRequestDto;
import com.elec_business.controller.dto.ChargingStationResponseDto;
import com.elec_business.controller.dto.ChargingStationUpdateRequestDto;
import com.elec_business.controller.mapper.ChargingStationMapper;
import com.elec_business.entity.User;
import com.elec_business.entity.ChargingStation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import org.springframework.data.domain.Pageable;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Charging Stations", description = "API de gestion des bornes de recharge (CRUD, Images, Recherche)")
public class ChargingStationController {

    private final ChargingStationBusiness chargingStationBusiness;
    private final ChargingStationMapper chargingStationMapper;

    // --- CREATE ---
    @Operation(
            summary = "Créer une nouvelle borne de recharge",
            description = "Permet à un propriétaire de créer une borne de recharge liée à un emplacement (Location). Nécessite l'upload d'une image."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Borne créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChargingStationResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides (image manquante, format incorrect)"),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Vous n'êtes pas le propriétaire de cet emplacement")
    })
    @PostMapping(value = "/charging_stations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ChargingStationResponseDto addChargingStation(
            @Parameter(description = "Les données de la borne (JSON)", required = true)
            @Valid @ModelAttribute ChargingStationRequestDto dto,

            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser,

            @Parameter(description = "Fichier image de la borne (JPG, PNG, WEBP)", required = true)
            @RequestPart(value = "image", required = false) MultipartFile image) throws AccessDeniedException {

        return chargingStationMapper.toDto(
                chargingStationBusiness.createChargingStation(chargingStationMapper.toEntity(dto), currentUser, image)
        );
    }

    // --- UPDATE ---
    @Operation(
            summary = "Mettre à jour une borne existante",
            description = "Permet de modifier le nom, la description, le prix, la puissance et L'IMAGE d'une borne."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borne mise à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChargingStationResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Vous n'êtes pas le propriétaire"),
            @ApiResponse(responseCode = "404", description = "Borne introuvable")
    })
    @PutMapping(value = "/charging_stations/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ChargingStationResponseDto updateStation(
            @Parameter(description = "ID de la borne à modifier", required = true) @PathVariable String id,
            @Valid @ModelAttribute ChargingStationUpdateRequestDto dto,

            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser,
            @Parameter(description = "Nouvelle image (optionnel)")
            @RequestPart(value = "image", required = false) MultipartFile image) throws AccessDeniedException {

        return chargingStationMapper.toDto(
                chargingStationBusiness.updateChargingStation(id, chargingStationMapper.toUpdateEntity(dto), currentUser, image)
        );
    }

    // --- GET ALL (Paginé) ---
    @Operation(
            summary = "Lister toutes les bornes (Version Résumée)",
            description = "Récupère une liste paginée des bornes. Retourne un format allégé (sans les avis) pour optimiser les performances de la liste."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page de bornes récupérée")
    })
    @GetMapping("/charging_stations")
    public Page<ChargingStationResponseDto> getAllChargingStations(
            @Parameter(description = "Paramètres de pagination (page, size, sort)") Pageable pageable) {
        return chargingStationBusiness.getAllChargingStations(pageable).map(chargingStationMapper::toSummaryDto);
    }

    // --- GET BY LOCATION ---
    @Operation(
            summary = "Lister les bornes d'un emplacement",
            description = "Récupère toutes les bornes rattachées à un ChargingLocation spécifique."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée")
    })
    @GetMapping("/charging_stations/location/{locationId}")
    public List<ChargingStationResponseDto> getChargingStationsByUser(
            @Parameter(description = "ID de l'emplacement (Location)", required = true) @PathVariable String locationId,@AuthenticationPrincipal User currentUser)  {
        return chargingStationBusiness.getByLocationId(locationId)
                .stream()
                .map(chargingStationMapper::toSummaryDto)
                .toList();
    }

    //--GET MY STATION ---
    @Operation(
            summary = "Lister les bornes d'un utilisateur",
            description = "Récupère toutes les bornes rattachées à l'utilisateur connecté."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée")
    })
    @GetMapping("/charging_stations/me")
    public List<ChargingStationResponseDto>getMyStations(@AuthenticationPrincipal User currentUser) {
        List<ChargingStation> stations = chargingStationBusiness.getMyStations(currentUser);

         List<ChargingStationResponseDto> dtos = stations.stream()
                .map(chargingStationMapper::toSummaryDto)
                .toList();
         return dtos;
    }

    // --- GET ONE ---
    @Operation(
            summary = "Obtenir le détail d'une borne",
            description = "Récupère les informations complètes d'une borne, y compris la liste des avis (Reviews)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Détail de la borne récupéré",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChargingStationResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Borne introuvable")
    })
    @GetMapping("/charging_stations/{id}")
    public ChargingStationResponseDto getStation(
            @Parameter(description = "ID de la borne", required = true) @PathVariable String id) {
        ChargingStation station = chargingStationBusiness.getChargingStationById(id);
        return chargingStationMapper.toDto(station);
    }

    // --- DELETE ---
    @Operation(
            summary = "Supprimer une borne",
            description = "Supprime définitivement une borne. Action irréversible réservée au propriétaire."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Borne supprimée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
            @ApiResponse(responseCode = "404", description = "Borne introuvable")
    })
    @DeleteMapping("/charging_stations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChargingStation(
            @Parameter(description = "ID de la borne à supprimer", required = true) @PathVariable String id,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser) throws AccessDeniedException {
        chargingStationBusiness.deleteChargingStationById(id, currentUser);
    }
}