package com.dero.opcg_api.repository;

import com.dero.opcg_api.model.CardSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardSetRepository extends JpaRepository<CardSet, String> {
}