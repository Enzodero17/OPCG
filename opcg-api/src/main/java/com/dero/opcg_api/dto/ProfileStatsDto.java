package com.dero.opcg_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileStatsDto {
    private String username;
    private int coins;
    private int totalBoostersOpened;
    private String favoriteVariantId;
    private String favoriteCardImageUrl;
    private String favoriteCardName;
    private int uniqueCardsOwned;
    private int totalCardsInGame;
    private int globalCompletionRatio;
}
