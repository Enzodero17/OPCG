package com.dero.opcg_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_missions", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "mission_id", "period_key"}))
@Data
public class UserMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(name = "period_key", nullable = false)
    private String periodKey;

    private int currentAmount = 0;
    private boolean isCompleted = false;
    private boolean isClaimed = false;
}