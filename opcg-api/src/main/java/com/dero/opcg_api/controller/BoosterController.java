package com.dero.opcg_api.controller;

import com.dero.opcg_api.model.CardVariant;
import com.dero.opcg_api.service.BoosterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boosters")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BoosterController {

    private final BoosterService boosterService;

    // http://localhost:8080/api/boosters/open/OP-01
    @GetMapping("/open/{setId}")
    public List<CardVariant> openBooster(@PathVariable String setId) {
        return boosterService.openEnglishBooster(setId);
    }
}
