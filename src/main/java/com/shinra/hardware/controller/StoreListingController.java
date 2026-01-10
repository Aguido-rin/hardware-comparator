package com.shinra.hardware.controller;

// Importa TU archivo DTO (el record que creaste)
import com.shinra.hardware.dto.PriceUpdateRequestDTO;
import com.shinra.hardware.dto.StoreListingDTO;
import com.shinra.hardware.service.StoreListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class StoreListingController {

    private final StoreListingService listingService;

    @Autowired
    public StoreListingController(StoreListingService listingService) {
        this.listingService = listingService;
    }

    // 1. Frontend: Ver precios de un producto
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StoreListingDTO>> getListingsByProduct(@PathVariable Long productId) {
        List<StoreListingDTO> listings = listingService.getListingsForProduct(productId);
        if (listings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(listings);
    }

    // 2. Scraper: Obtener TODAS las URLs para espiar
    // (Ahora funcionará porque ya agregamos el método a la Interfaz)
    @GetMapping("/all")
    public ResponseEntity<List<StoreListingDTO>> getAllListings() {
        return ResponseEntity.ok(listingService.getAllListings());
    }

    // 3. Scraper: Actualizar el precio
    @PostMapping("/{id}/update")
    public ResponseEntity<Void> updatePrice(@PathVariable Long id, @RequestBody PriceUpdateRequestDTO request) {
        // Usamos los métodos del Record: .price() y .inStock()
        listingService.updateListingPrice(id, request.price(), request.inStock());
        return ResponseEntity.ok().build();
    }
}