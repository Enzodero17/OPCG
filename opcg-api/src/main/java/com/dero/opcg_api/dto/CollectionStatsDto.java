package com.dero.opcg_api.dto;

import lombok.Data;

@Data
public class CollectionStatsDto {

    private String setId;
    private int totalCardsInSet;
    private int uniqueCardsOwned;
    private double completionPercentage;

    public CollectionStatsDto(String setId, int totalCardsInSet, int uniqueCardsOwned) {
        this.setId = setId;
        this.totalCardsInSet = totalCardsInSet;
        this.uniqueCardsOwned = uniqueCardsOwned;

        if (totalCardsInSet > 0) {
            this.completionPercentage = Math.round(((double) uniqueCardsOwned / totalCardsInSet) * 10000.0) / 100.0;
        } else {
            this.completionPercentage = 0.0;
        }
    }
}
