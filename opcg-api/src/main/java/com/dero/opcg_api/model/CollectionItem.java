package com.dero.opcg_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "collection_items")
@Data
public class CollectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "card_variant_id", nullable = false)
    private CardVariant cardVariant;

    @Column(nullable = false)
    private Integer quantity = 1;
}