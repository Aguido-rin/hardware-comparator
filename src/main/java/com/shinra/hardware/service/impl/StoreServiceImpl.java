package com.shinra.hardware.service.impl;

import com.shinra.hardware.model.Store;
import com.shinra.hardware.repository.StoreRepository;
import com.shinra.hardware.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Autowired
    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Store> findAllStores() {
        return storeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Store> findStoreById(Integer id) {
        return storeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Store> findStoreByName(String name) {
        return storeRepository.findByName(name);
    }

    @Override
    @Transactional
    public Store saveStore(Store store) {
        return storeRepository.save(store);
    }

    @Override
    @Transactional
    public void deleteStore(Integer id) {
        storeRepository.deleteById(id);
    }
}
