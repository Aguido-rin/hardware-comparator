package com.shinra.hardware.service.impl;

import com.shinra.hardware.dto.StoreListingDTO;
import com.shinra.hardware.model.PriceHistory;
import com.shinra.hardware.model.StoreListing;
import com.shinra.hardware.repository.PriceHistoryRepository;
import com.shinra.hardware.repository.StoreListingRepository;
import com.shinra.hardware.service.StoreListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreListingServiceImpl implements StoreListingService {

    private final StoreListingRepository storeListingRepository;
    private final PriceHistoryRepository priceHistoryRepository; // Necesitamos crear este repo rápido

    @Autowired
    public StoreListingServiceImpl(StoreListingRepository storeListingRepository,
                                   PriceHistoryRepository priceHistoryRepository) {
        this.storeListingRepository = storeListingRepository;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreListingDTO> getListingsForProduct(Long productId) {
        return storeListingRepository.findByProduct_IdOrderByCurrentPriceAsc(productId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StoreListing saveListing(StoreListing listing) {
        return storeListingRepository.save(listing);
    }

    @Override
    @Transactional
    public void updateListingPrice(Long listingId, BigDecimal newPrice, Boolean inStock) {
        StoreListing listing = storeListingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing no encontrado: " + listingId));

        // 1. Actualizamos el listing actual
        listing.setCurrentPrice(newPrice);
        listing.setIsInStock(inStock);
        listing.setLastCheckedAt(OffsetDateTime.now());
        storeListingRepository.save(listing);

        // 2. Guardamos en el historial (PriceHistory)
        // Esto replica la lógica de tu SP 'sp_record_price_scrape' pero en Java
        PriceHistory history = new PriceHistory();
        history.setListing(listing);
        history.setPrice(newPrice);
        // recordedAt se llena solo por la entidad o DB, o lo seteamos aquí si es necesario
        priceHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void deleteListing(Long listingId) {
        storeListingRepository.deleteById(listingId);
    }

    // --- Mapper Privado ---
    private StoreListingDTO mapToDTO(StoreListing entity) {
        return new StoreListingDTO(
                entity.getId(),
                entity.getStore().getName(),
                entity.getStore().getLogoUrl(),
                entity.getCurrentPrice(),
                entity.getAffiliateLinkGenerated() != null ? entity.getAffiliateLinkGenerated() : entity.getUrlSource(),
                entity.getIsInStock(),
                entity.getLastCheckedAt()
        );
    }
}
