package com.dero.opcg_api.controller;

import com.dero.opcg_api.dto.LibraryItemDto;
import com.dero.opcg_api.model.CardVariant;
import com.dero.opcg_api.model.CollectionItem;
import com.dero.opcg_api.repository.CardVariantRepository;
import com.dero.opcg_api.repository.CollectionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LibraryController {

    private final CardVariantRepository variantRepo;
    private final CollectionItemRepository collectionRepo;

    @GetMapping("/sets/{setId}")
    public List<CardVariant> getSetLibrary(@PathVariable String setId) {
        return variantRepo.findAllBySetId(setId);
    }

    @GetMapping("/sets/{setId}/user/{userId}")
    public List<LibraryItemDto> getSetLibraryForUser(@PathVariable String setId, @PathVariable UUID userId) {

        // On récupère TOUTES les cartes de l'extension Romance Dawn
        List<CardVariant> allVariantsInSet = variantRepo.findAllBySetId(setId);

        // On récupère l'inventaire complet du joueur
        List<CollectionItem> userInventory = collectionRepo.findByUserId(userId);

        // OPTIMISATION MAGIQUE : On transforme l'inventaire en "Dictionnaire" (Map).
        // La clé = L'ID de la carte (ex: OP01-001) | La valeur = La quantité possédée (ex: 3)
        // Cela évite de faire des boucles dans des boucles qui ralentiraient le serveur !
        Map<String, Integer> ownedCardsMap = userInventory.stream()
                .collect(Collectors.toMap(
                        item -> item.getCardVariant().getId(),
                        CollectionItem::getQuantity,
                        (existing, replacement) -> existing
                ));

        // On fusionne les données : on parcourt chaque carte du Set, on regarde dans le dictionnaire, et on crée le DTO.
        return allVariantsInSet.stream().map(variant -> {
            int quantity = ownedCardsMap.getOrDefault(variant.getId(), 0);
            boolean isOwned = quantity > 0;
            return new LibraryItemDto(variant, isOwned, quantity);
        }).collect(Collectors.toList());
    }
}