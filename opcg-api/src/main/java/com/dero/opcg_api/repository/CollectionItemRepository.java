package com.dero.opcg_api.repository;

import com.dero.opcg_api.model.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {
    List<CollectionItem> findByUserId(UUID userId);

    CollectionItem findByUserIdAndCardVariantId(UUID userId, String variantId);

    @Query("SELECT COUNT(c) FROM CollectionItem c WHERE c.user.id = :userId AND c.cardVariant.card.cardSet.id = :setId")
    long countOwnedVariantsBySet(UUID userId, String setId);
}