package com.dero.opcg_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @OneToMany(mappedBy = "cardSet", cascade = CascadeType.ALL)
    private List<Card> cards;
}
