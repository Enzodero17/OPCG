package com.dero.opcg_api.repository;

import com.dero.opcg_api.model.CardVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CardVariantRepository extends JpaRepository<CardVariant, String> {
    @Query("SELECT cv FROM CardVariant cv WHERE cv.card.cardSet.id = :setId AND cv.card.rarity = :rarity AND cv.variantType = 'Standard'")
    List<CardVariant> findStandardByRarity(String setId, String rarity);

    @Query("SELECT cv FROM CardVariant cv WHERE cv.card.cardSet.id = :setId AND cv.variantType != 'Standard'")
    List<CardVariant> findHits(String setId);

    @Query("SELECT cv FROM CardVariant cv WHERE cv.card.cardSet.id = :setId ORDER BY cv.id ASC")
    List<CardVariant> findAllBySetId(String setId);
}