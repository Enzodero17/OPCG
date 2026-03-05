package com.dero.opcg_api.service;

import com.dero.opcg_api.model.CardVariant;
import com.dero.opcg_api.repository.CardVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BoosterService {

    private final CardVariantRepository variantRepo;
    private final Random random = new Random();

    public List<CardVariant> openEnglishBooster(String setId) {
        List<CardVariant> booster = new ArrayList<>();

        // Récupérer les "pools" de cartes depuis la base de données
        List<CardVariant> commons = variantRepo.findStandardByRarity(setId, "C");
        List<CardVariant> uncommons = variantRepo.findStandardByRarity(setId, "UC");
        List<CardVariant> rares = variantRepo.findStandardByRarity(setId, "R");
        List<CardVariant> superRares = variantRepo.findStandardByRarity(setId, "SR");
        List<CardVariant> secretRares = variantRepo.findStandardByRarity(setId, "SEC");
        List<CardVariant> leaders = variantRepo.findStandardByRarity(setId, "L");
        List<CardVariant> hits = variantRepo.findHits(setId); // Les Parallels/Mangas

        // Si la base n'est pas encore remplie, on évite de faire planter le code
        if (commons.isEmpty() || uncommons.isEmpty()) {
            throw new RuntimeException("L'extension " + setId + " ne contient pas assez de cartes.");
        }

        // Slot 1 à 8 : 8 Cartes Communes
        for (int i = 0; i < 8; i++) {
            booster.add(getRandomCard(commons));
        }

        // Slot 9 et 10 : 2 Cartes Peu Communes
        for (int i = 0; i < 2; i++) {
            booster.add(getRandomCard(uncommons));
        }

        // Slot 11 : Le slot Rare ou supérieur (70% Rare, 25% Super Rare, 5% Secret Rare)
        int rareRoll = random.nextInt(100); // Tire un nombre entre 0 et 99
        if (rareRoll < 70) {
            booster.add(getRandomCard(rares));
        } else if (rareRoll < 95) {
            booster.add(getRandomCard(superRares));
        } else {
            booster.add(getRandomCard(secretRares.isEmpty() ? superRares : secretRares));
        }

        // Slot 12 : Le slot Leader ou Hit (80% Leader, 20% Hit/Parallel)
        int hitRoll = random.nextInt(100);
        if (hitRoll < 80) {
            booster.add(getRandomCard(leaders));
        } else {
            booster.add(getRandomCard(hits.isEmpty() ? leaders : hits));
        }

        return booster;
    }

    // Petite méthode utilitaire pour piocher une carte au hasard dans une liste
    private CardVariant getRandomCard(List<CardVariant> pool) {
        if (pool == null || pool.isEmpty()) return null;
        int randomIndex = random.nextInt(pool.size());
        return pool.get(randomIndex);
    }
}