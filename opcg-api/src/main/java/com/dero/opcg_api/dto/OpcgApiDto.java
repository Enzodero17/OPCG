package com.dero.opcg_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpcgApiDto {

    @JsonProperty("set_name") private String setName;
    @JsonProperty("set_id") private String setId;
    @JsonProperty("card_set_id") private String cardId;
    @JsonProperty("card_name") private String cardName;
    @JsonProperty("rarity") private String rarity;
    @JsonProperty("card_type") private String cardType;
    @JsonProperty("card_color") private String cardColor;
    @JsonProperty("attribute") private String attribute;

    @JsonProperty("card_cost") private String cardCost;
    @JsonProperty("card_power") private String cardPower;
    @JsonProperty("life") private String life;
    @JsonProperty("counter_amount") private String counterAmount;

    @JsonProperty("sub_types") private String subTypes;
    @JsonProperty("card_text") private String cardText;
    @JsonProperty("card_image_id") private String cardImageId;
    @JsonProperty("card_image") private String cardImage;
    @JsonProperty("market_price") private Double marketPrice;
}
