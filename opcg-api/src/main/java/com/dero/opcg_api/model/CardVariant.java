package com.dero.opcg_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "card_variants")
@Data
public class CardVariant {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    private String imageUrl;
    private String variantType;

    private Double marketPrice;
    private Double inventoryPrice;

    private LocalDate dateScraped;
}