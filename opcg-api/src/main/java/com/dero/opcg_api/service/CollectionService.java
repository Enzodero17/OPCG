package com.dero.opcg_api.service;

import com.dero.opcg_api.dto.RewardResponseDto;
import com.dero.opcg_api.dto.SellRequestDto;
import com.dero.opcg_api.model.CardVariant;
import com.dero.opcg_api.model.CollectionItem;
import com.dero.opcg_api.model.User;
import com.dero.opcg_api.repository.CardRepository;
import com.dero.opcg_api.repository.CollectionItemRepository;
import com.dero.opcg_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionItemRepository collectionRepo;
    private final UserRepository userRepo;
    private final MissionService missionService;

    @Transactional
    public void addCardsToUserCollection(UUID userId, List<CardVariant> pulledCards) {

        // On vérifie que le joueur existe bien
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable !"));

        // On charge l'inventaire du joueur
        Map<String, CollectionItem> existentItemsByVariantId = collectionRepo.findByUserId(userId).stream()
                .collect(Collectors.toMap(item -> item.getCardVariant().getId(),
                        item -> item, (a, b) -> a));

        Set<String> raritiesOwnedBefore = existentItemsByVariantId.values().stream()
                .map(item -> item.getCardVariant().getCard().getRarity())
                .collect(Collectors.toSet());

        // On parcourt les 12 cartes qu'il vient de tirer
        for (CardVariant card : pulledCards) {
            addOrIncrementCard(user, card, existentItemsByVariantId);
        }

        Set<String> newRaritiesThisPull = new java.util.HashSet<>();

        for (CardVariant card : pulledCards) {
            String rarity = card.getCard().getRarity();

            if(!raritiesOwnedBefore.contains(rarity) && newRaritiesThisPull.add(rarity)) {
                missionService.processAction(userId, "FIRST_CARD_" + rarity, 1);
            }
        }

        missionService.updatePeakProgress(userId, "UNIQUE_CARDS_OWNED", existentItemsByVariantId.size());
    }

    private void addOrIncrementCard(User user, CardVariant card, Map<String, CollectionItem> existingItemsByVariantId) {
        CollectionItem existingItem = collectionRepo.findByUserIdAndCardVariantId(user.getId(), card.getId());

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            collectionRepo.save(existingItem);
            return;
        }

        try {
            CollectionItem newItem = new CollectionItem();
            newItem.setUser(user);
            newItem.setCardVariant(card);
            newItem.setQuantity(1);
            collectionRepo.saveAndFlush(newItem);
            existingItemsByVariantId.put(card.getId(), newItem);
        } catch (DataIntegrityViolationException e) {
            CollectionItem concurrentItem = collectionRepo.findByUserIdAndCardVariantId(user.getId(), card.getId());

            if (concurrentItem != null) {
                concurrentItem.setQuantity(concurrentItem.getQuantity() + 1);
                collectionRepo.save(concurrentItem);
                existingItemsByVariantId.put(card.getId(), concurrentItem);
            }
        }
    }

    @Transactional
    public String sellCard(UUID userId, String variantId) {

        // On cherche la carte précise dans l'inventaire du joueur
        CollectionItem item = collectionRepo.findByUserIdAndCardVariantId(userId, variantId);

        // On vérifie qu'il possède bien la carte
        if (item == null || item.getQuantity() <= 0) {
            throw new RuntimeException("Tu ne possèdes pas cette carte ou tu l'as déjà vendue !");
        }

        // On calcule le prix de rachat
        CardVariant variant = item.getCardVariant();
        int sellPrice = calculateSellPrice(variant);

        // On paie le joueur
        User user = item.getUser();
        user.setCoins(user.getCoins() + sellPrice);
        userRepo.save(user);

        // On retire la carte de son inventaire
        if (item.getQuantity() > 1) {
            // S'il en a plusieurs, on enlève juste 1 à la quantité
            item.setQuantity(item.getQuantity() - 1);
            collectionRepo.save(item);
        } else {
            // S'il n'en a qu'une seule, on supprime carrément la ligne de la base de données
            collectionRepo.delete(item);
        }

        missionService.processAction(userId, "SELL_CARD", 1);

        return "Carte vendue pour " + sellPrice + " pièces ! Ton nouveau solde est de " + user.getCoins() + " pièces.";
    }

    @Transactional
    public RewardResponseDto sellDuplicateCard(UUID userId, List<SellRequestDto> itemsToSell) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable !"));

        int totalCoinsEarned = 0;
        int totalCardsSold = 0;

        // Parcours du panier
        for (SellRequestDto requestDto : itemsToSell) {

            if (requestDto.getQuantityToSell() <= 0) {
                throw new RuntimeException("La quantité à vendre doit être positive pour la carte " + requestDto.getVariantId());
            }

            CollectionItem item = collectionRepo.findByUserIdAndCardVariantId(userId, requestDto.getVariantId());

            if (item == null) {
                throw new RuntimeException("Tu ne possèdes pas cette carte " + requestDto.getVariantId());
            }

            int remainingQuantity = item.getQuantity() - requestDto.getQuantityToSell();
            if (remainingQuantity < 1) {
                throw new RuntimeException("Tu dois garder au moins 1 exemplaire de la carte " + requestDto.getVariantId());
            }

            int sellPrice = calculateSellPrice(item.getCardVariant());
            int earnings = sellPrice * requestDto.getQuantityToSell();

            item.setQuantity(remainingQuantity);
            collectionRepo.save(item);

            totalCardsSold += requestDto.getQuantityToSell();
            totalCoinsEarned += earnings;
        }

        // On paie le joueur
        user.setCoins(user.getCoins() + totalCoinsEarned);
        userRepo.save(user);

        missionService.processAction(userId, "SELL_CARD", totalCardsSold);

        String message = "Vente réussie ! " + totalCardsSold + " doublons vendus pour " + totalCoinsEarned + " pièces.";
        return new RewardResponseDto(message, user.getCoins());
    }

    private int calculateSellPrice(CardVariant variant) {
        int basePrice = 0;

        // On lit la rareté sur la carte d'origine
        switch (variant.getCard().getRarity()) {
            case "UC": basePrice = 10; break;
            case "R": basePrice = 50; break;
            case "L": basePrice = 100; break;
            case "SR": basePrice = 200; break;
            case "SEC": basePrice = 800; break;
            default: basePrice = 5;
        }

        if (!"Standard".equals(variant.getVariantType())) {
            basePrice = basePrice * 5;
        }

        return basePrice;
    }
}
