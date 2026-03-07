package com.dero.opcg_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "missions")
@Data
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private int rewardCoins;

    private String actionType;

    private int targetAmount;
}
