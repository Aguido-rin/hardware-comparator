package com.shinra.hardware.controller;

import com.shinra.hardware.dto.StoreListingDTO;
import com.shinra.hardware.service.StoreListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class StoreListingController {

    private final StoreListingService storeListingService;

    @Autowired
    public StoreListingController(StoreListingService storeListingService) {
        this.storeListingService = storeListingService;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StoreListingDTO>> getListingsByProduct(@PathVariable Long productId) {
        List<StoreListingDTO> listings = storeListingService.getListingsForProduct(productId);
        if (listings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(listings);
    }

}
