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
}
