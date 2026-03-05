package com.dero.opcg_api.dto;

import com.dero.opcg_api.model.CardVariant;
import lombok.Data;

@Data
public class LibraryItemDto {

    private CardVariant cardVariant;
    private boolean isOwned;
    private int quantity;

    public LibraryItemDto(CardVariant cardVariant, boolean isOwned, int quantity) {
        this.cardVariant = cardVariant;
        this.isOwned = isOwned;
        this.quantity = quantity;
    }
}
