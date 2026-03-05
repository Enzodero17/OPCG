package com.dero.opcg_api.controller;

import com.dero.opcg_api.model.CollectionItem;
import com.dero.opcg_api.repository.CollectionItemRepository;
import com.dero.opcg_api.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/collection")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CollectionController {

    private final CollectionItemRepository collectionRepo;
    private final CollectionService collectionService;

    @GetMapping("/{userId}")
    public List<CollectionItem> getUserCollection(@PathVariable UUID userId) {
        return collectionRepo.findByUserId(userId);
    }

    @GetMapping("/{userId}/sell/{variantId}")
    public String sellCard(@PathVariable UUID userId, @PathVariable String variantId) {
        return collectionService.sellCard(userId, variantId);
    }
}