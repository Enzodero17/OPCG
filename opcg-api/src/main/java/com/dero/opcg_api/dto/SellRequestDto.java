package com.dero.opcg_api.dto;

import lombok.Data;

@Data
public class SellRequestDto {
    private String variantId;
    private int quantityToSell;
}
