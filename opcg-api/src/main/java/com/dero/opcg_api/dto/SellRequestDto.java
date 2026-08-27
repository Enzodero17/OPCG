package com.dero.opcg_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SellRequestDto {

    @NotBlank(message = "L'identifiant de la carte est obligatoire.")
    private String variantId;

    @Positive(message = "La quantité à vendre doit être positive.")
    private int quantityToSell;
}
