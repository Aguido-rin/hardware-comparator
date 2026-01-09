package com.shinra.hardware.controller;

import com.shinra.hardware.dto.PriceHistoryPointDTO;
import com.shinra.hardware.service.PriceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    @Autowired
    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<PriceHistoryPointDTO>> getHistory(@PathVariable Long listingId) {
        List<PriceHistoryPointDTO> points = priceHistoryService.getHistoryForListing(listingId);
        return ResponseEntity.ok(points);
    }
}
