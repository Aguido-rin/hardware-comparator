package com.shinra.hardware.repository;

import com.shinra.hardware.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {

    // Encontrar una tienda por su nombre
    Optional<Store> findByName(String name);
}
