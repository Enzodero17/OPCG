package com.dero.opcg_api.controller;

import com.dero.opcg_api.model.CollectionItem;
import com.dero.opcg_api.repository.CollectionItemRepository;
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

    @GetMapping("/{userId}")
    public List<CollectionItem> getUserCollection(@PathVariable UUID userId) {
        return collectionRepo.findByUserId(userId);
    }
}