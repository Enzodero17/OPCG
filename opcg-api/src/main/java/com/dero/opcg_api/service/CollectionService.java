package com.dero.opcg_api.service;

import com.dero.opcg_api.model.CardVariant;
import com.dero.opcg_api.model.CollectionItem;
import com.dero.opcg_api.model.User;
import com.dero.opcg_api.repository.CollectionItemRepository;
import com.dero.opcg_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionItemRepository collectionRepo;
    private final UserRepository userRepo;

    @Transactional
    public void addCardsToUserCollection(UUID userId, List<CardVariant> pulledCards) {

        // On vérifie que le joueur existe bien
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable !"));

        // On parcourt les 12 cartes qu'il vient de tirer
        for (CardVariant card : pulledCards) {

            // On regarde s'il possède DÉJÀ cette carte exacte dans son inventaire
            CollectionItem existingItem = collectionRepo.findByUserIdAndCardVariantId(userId, card.getId());

            if (existingItem != null) {
                // S'il l'a déjà, on augmente juste la quantité de +1
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                collectionRepo.save(existingItem);
            } else {
                // S'il ne l'a pas, on crée une nouvelle ligne dans son inventaire
                CollectionItem newItem = new CollectionItem();
                newItem.setUser(user);
                newItem.setCardVariant(card);
                newItem.setQuantity(1);
                collectionRepo.save(newItem);
            }
        }
    }

    @Transactional
    public String sellCard(UUID userId, String variantId) {

        // On cherche la carte précise dans l'inventaire du joueur
        CollectionItem item = collectionRepo.findByUserIdAndCardVariantId(userId, variantId);

        // Sécurité : On vérifie qu'il possède bien la carte
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

        return "Carte vendue pour " + sellPrice + " pièces ! Ton nouveau solde est de " + user.getCoins() + " pièces.";
    }

    private int calculateSellPrice(CardVariant variant) {
        int basePrice = 0;

        // On lit la rareté sur la carte d'origine
        switch (variant.getCard().getRarity()) {
            case "C": basePrice = 5; break;
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
