package com.dero.opcg_api.controller;

import com.dero.opcg_api.dto.CollectionStatsDto;
import com.dero.opcg_api.dto.ProfileStatsDto;
import com.dero.opcg_api.dto.RewardResponseDto;
import com.dero.opcg_api.dto.SellRequestDto;
import com.dero.opcg_api.model.CollectionItem;
import com.dero.opcg_api.model.User;
import com.dero.opcg_api.repository.CardVariantRepository;
import com.dero.opcg_api.repository.CollectionItemRepository;
import com.dero.opcg_api.repository.UserRepository;
import com.dero.opcg_api.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/collection")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CollectionController {

    private final CollectionItemRepository collectionRepo;
    private final CollectionService collectionService;
    private final CardVariantRepository variantRepo;
    private final UserRepository userRepo;

    @GetMapping("/{userId}")
    public List<CollectionItem> getUserCollection(@PathVariable UUID userId) {
        return collectionRepo.findByUserId(userId);
    }

    @GetMapping("/{userId}/sell/{variantId}")
    public String sellCard(@PathVariable UUID userId, @PathVariable String variantId) {
        return collectionService.sellCard(userId, variantId);
    }

    @PostMapping("/sell/{userId}")
    public RewardResponseDto sellCards(@PathVariable UUID userId, @RequestBody List<SellRequestDto> itemsToSell) {
        return collectionService.sellDuplicateCard(userId, itemsToSell);
    }

    @GetMapping("/{userId}/stats/{setId}")
    public CollectionStatsDto getCollectionStats(@PathVariable UUID userId, @PathVariable String setId) {

        // On demande le total absolu au CardVariantRepository
        int totalInSet = (int) variantRepo.countBySetId(setId);

        // On demande le total du joueur au CollectionItemRepository
        int ownedByUser = (int) collectionRepo.countOwnedVariantsBySet(userId, setId);

        // On crée notre DTO qui va calculer le pourcentage et on le renvoie
        return new CollectionStatsDto(setId, totalInSet, ownedByUser);
    }

    @GetMapping("/{userId}/profile-stats")
    public ProfileStatsDto getProfileStats(@PathVariable UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Joueur introuvable"));

        // Calcul des stats de cartes
        int totalInGame = (int) variantRepo.count();

        int ownedUnique = collectionRepo.findByUserId(userId).size();

        int globalRatio = totalInGame > 0 ? Math.round(((float) ownedUnique / totalInGame) * 100) : 0;

        // Gestion de la carte préférée
        String favUrl = null;
        String favName = null;
        if (user.getFavoriteVariantId() != null) {
            var variantOpt = variantRepo.findById(user.getFavoriteVariantId());
            if (variantOpt.isPresent()) {
                favUrl = variantOpt.get().getImageUrl();
                favName = variantOpt.get().getCard().getName();
            }
        }

        return new ProfileStatsDto(
                user.getUsername(),
                user.getCoins(),
                user.getTotalBoostersOpened(),
                user.getFavoriteVariantId(),
                favUrl,
                favName,
                ownedUnique,
                totalInGame,
                globalRatio
        );
    }

    @PostMapping("/{userId}/favorite/{variantId}")
    public ResponseEntity<String> updateFavoriteCard(@PathVariable UUID userId, @PathVariable String variantId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Joueur introuvable"));

        user.setFavoriteVariantId(variantId);
        userRepo.save(user);
        return ResponseEntity.ok("Carte préférée mise à jour !");
    }
}