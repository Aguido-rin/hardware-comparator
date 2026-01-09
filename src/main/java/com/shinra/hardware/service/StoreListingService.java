package com.shinra.hardware.service;

import com.shinra.hardware.dto.StoreListingDTO;
import com.shinra.hardware.model.StoreListing;

import java.math.BigDecimal;
import java.util.List;

public interface StoreListingService {

    List<StoreListingDTO> getListingsForProduct(Long productId);

    StoreListing saveListing(StoreListing listing);

    void updateListingPrice(Long listingId, BigDecimal newPrice, Boolean inStock);

    void deleteListing(Long listingId);
}
