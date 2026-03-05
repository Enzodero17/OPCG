package com.dero.opcg_api.service;

import com.dero.opcg_api.model.User;
import com.dero.opcg_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final UserRepository userRepo;
    private final int DAILY_REWARD_AMOUNT = 1000;

    public String claimDailyReward(UUID userId) {

        // On cherche le joueur
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Joueur introuvable !"));

        LocalDate today = LocalDate.now();

        // On vérifie si la date n'est pas nulle ET que le jour enregistré est le même qu'aujourd'hui
        if (user.getLastDailyReward() != null && user.getLastDailyReward().toLocalDate().isEqual(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tu as déjà récupéré ta récompense aujourd'hui ! Reviens demain à partir de minuit.");
        }

        // Si tout est bon, on paie le joueur
        user.setCoins(user.getCoins() + DAILY_REWARD_AMOUNT);

        // On met à jour son "chronomètre" avec l'heure exacte actuelle
        user.setLastDailyReward(LocalDateTime.now());

        userRepo.save(user);

        return "Félicitations ! Tu as reçu " + DAILY_REWARD_AMOUNT + " pièces. Ton nouveau solde est de " + user.getCoins() + " pièces.";
    }
}