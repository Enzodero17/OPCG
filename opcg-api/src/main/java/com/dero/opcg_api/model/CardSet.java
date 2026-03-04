package com.dero.opcg_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "card_sets")
@Data
public class CardSet {

    @Id
    private String id;

    private String name;

    @OneToMany(mappedBy = "cardSet", cascade = CascadeType.ALL)
    private List<Card> cards;
}
