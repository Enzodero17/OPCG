package com.dero.opcg_api.controller;

import com.dero.opcg_api.model.CardVariant;
import com.dero.opcg_api.service.BoosterService;
import com.dero.opcg_api.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/boosters")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BoosterController {

    private final BoosterService boosterService;
    private final CollectionService collectionService;

    @GetMapping("/open/{setId}/{userId}")
    public List<CardVariant> openBoosterAndSave(@PathVariable String setId, @PathVariable UUID userId) {

        // L'algorithme tire les 12 cartes
        List<CardVariant> booster = boosterService.openEnglishBooster(setId);

        // On enregistre ces 12 cartes dans l'inventaire du joueur
        collectionService.addCardsToUserCollection(userId, booster);

        // On renvoie les cartes au frontend pour l'animation d'ouverture
        return booster;
    }
}
