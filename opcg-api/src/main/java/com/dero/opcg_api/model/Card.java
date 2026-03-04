package com.dero.opcg_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "cards")
@Data
public class Card {

    @Id
    private String id;

    private String name;
    private String rarity;
    private String type;
    private String color;
    private String attribute;

    private Integer cost;
    private Integer power;
    private Integer life;
    private Integer counter;

    private String subTypes;

    @Column(columnDefinition = "TEXT")
    private String effectText;

    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private CardSet cardSet;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<CardVariant> variants;
}