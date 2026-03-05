package com.dero.opcg_api.service;

import com.dero.opcg_api.dto.OpcgApiDto;
import com.dero.opcg_api.model.*;
import com.dero.opcg_api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardSyncService {

    private final CardSetRepository setRepo;
    private final CardRepository cardRepo;
    private final CardVariantRepository variantRepo;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public String syncCardsFromApi() {
        int count = 0;
        int maxCardsInSet = 121;
        String targetSetId = "OP-01";

        System.out.println("Début de la synchronisation ciblée de l'extension " + targetSetId + "...");

        for (int i = 1; i <= maxCardsInSet; i++) {

            String cardNumber = String.format("%03d", i);
            String targetCardId = "OP01-" + cardNumber;
            String apiUrl = "https://optcgapi.com/api/sets/card/" + targetCardId + "/";

            try {
                OpcgApiDto[] cardsData = restTemplate.getForObject(apiUrl, OpcgApiDto[].class);

                if (cardsData != null) {
                    for (OpcgApiDto dto : cardsData) {

                        if (!targetSetId.equals(dto.getSetId())) {
                            System.out.println("  -> Ignoré : " + dto.getCardName() + " (Appartient au set " + dto.getSetId() + ")");
                            continue;
                        }

                        // Gérer l'extension
                        CardSet set = setRepo.findById(dto.getSetId()).orElseGet(() -> {
                            CardSet newSet = new CardSet();
                            newSet.setId(dto.getSetId());
                            newSet.setName(dto.getSetName());
                            return setRepo.save(newSet);
                        });

                        // Gérer la carte (Règles et Stats)
                        Card card = cardRepo.findById(dto.getCardId()).orElseGet(() -> {
                            Card newCard = new Card();
                            newCard.setId(dto.getCardId());
                            newCard.setName(dto.getCardName().replaceAll(" \\(.*\\)", "").trim());
                            newCard.setRarity(dto.getRarity());
                            newCard.setType(dto.getCardType());
                            newCard.setColor(dto.getCardColor());
                            newCard.setAttribute(dto.getAttribute());
                            newCard.setSubTypes(dto.getSubTypes());
                            newCard.setEffectText(dto.getCardText());
                            newCard.setCardSet(set);

                            if (dto.getCardCost() != null) newCard.setCost(Integer.parseInt(dto.getCardCost()));
                            if (dto.getCardPower() != null) newCard.setPower(Integer.parseInt(dto.getCardPower()));
                            if (dto.getLife() != null) newCard.setLife(Integer.parseInt(dto.getLife()));

                            return cardRepo.save(newCard);
                        });

                        // Gérer la variante (L'Image)
                        if (!variantRepo.existsById(dto.getCardImageId())) {
                            CardVariant variant = new CardVariant();
                            variant.setId(dto.getCardImageId());
                            variant.setCard(card);
                            variant.setImageUrl(dto.getCardImage());
                            variant.setMarketPrice(dto.getMarketPrice());

                            // Détermination plus fine du type de variante basé sur le nom fourni par l'API
                            String nameForVariant = dto.getCardName();
                            if (nameForVariant.contains("(Manga)")) {
                                variant.setVariantType("Manga Rare");
                            } else if (nameForVariant.contains("(Alternate Art)")) {
                                variant.setVariantType("Alternate Art");
                            } else if (nameForVariant.contains("(Parallel)") || dto.getCardImageId().contains("_p")) {
                                variant.setVariantType("Parallel");
                            } else {
                                variant.setVariantType("Standard");
                            }

                            variantRepo.save(variant);
                            count++;
                            System.out.println("Ajouté : " + variant.getCard().getName() + " - " + variant.getVariantType() + " [" + variant.getId() + "]");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ Erreur ou carte introuvable pour : " + targetCardId);
            }
        }

        return "Synchronisation OP-01 terminée ! " + count + " nouvelles variantes exclusives au set " + targetSetId + " ajoutées.";
    }
}