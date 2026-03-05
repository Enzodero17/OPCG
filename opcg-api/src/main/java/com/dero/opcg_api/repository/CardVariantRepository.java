package com.dero.opcg_api.repository;

import com.dero.opcg_api.model.CardVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CardVariantRepository extends JpaRepository<CardVariant, String> {
    List<CardVariant> findByCardId(String cardId);
}