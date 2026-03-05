package com.dero.opcg_api.controller;

import com.dero.opcg_api.model.CardVariant;
import com.dero.opcg_api.model.User;
import com.dero.opcg_api.repository.UserRepository;
import com.dero.opcg_api.service.BoosterService;
import com.dero.opcg_api.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/boosters")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BoosterController {

    private final BoosterService boosterService;
    private final CollectionService collectionService;
    private final UserRepository userRepository;

    private final int BOOSTER_PRICE = 500;

    @GetMapping("/open/{setId}/{userId}")
    public List<CardVariant> openBoosterAndSave(@PathVariable String setId, @PathVariable UUID userId) {

        // On cherche le joueur dans la base de données
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Joueur introuvable !"));

        // LA CAISSE ENREGISTREUSE : On vérifie s'il a assez de pièces
        if (user.getCoins() < BOOSTER_PRICE) {
            // S'il est trop pauvre, on bloque tout et on renvoie une erreur au site web !
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Fonds insuffisants. Il te faut " + BOOSTER_PRICE + " pièces, mais tu n'en as que " + user.getCoins() + ".");
        }

        // LE PAIEMENT : On lui retire ses 500 pièces et on sauvegarde son nouveau solde
        user.setCoins(user.getCoins() - BOOSTER_PRICE);
        userRepository.save(user);

        // L'algorithme tire les 12 cartes
        List<CardVariant> booster = boosterService.openEnglishBooster(setId);

        // On enregistre ces 12 cartes dans l'inventaire du joueur
        collectionService.addCardsToUserCollection(userId, booster);

        // On renvoie les cartes pour l'animation d'ouverture
        return booster;
    }
}