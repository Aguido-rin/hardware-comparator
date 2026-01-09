package com.shinra.hardware.service;

import com.shinra.hardware.model.Store;
import java.util.List;
import java.util.Optional;

public interface StoreService {
    List<Store> findAllStores();
    Optional<Store> findStoreById(Integer id);
    Optional<Store> findStoreByName(String name); // Vital para el scraper
    Store saveStore(Store store);
    void deleteStore(Integer id);
}
