package com.dero.opcg_api.repository;

import com.dero.opcg_api.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {
    List<Card> findByColor(String color);
    List<Card> findByCardSetId(String setId);
}