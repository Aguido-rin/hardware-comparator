package com.shinra.hardware.repository;

import com.shinra.hardware.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Buscar productos por marca (Ej: "NVIDIA", "AMD", "Intel")
    List<Product> findByBrand(String brand);

    // Buscar un producto por su nombre de modelo exacto
    Optional<Product> findByModelName(String modelName);

    // Buscar productos cuyo nombre de modelo contenga una cadena específica (IgnoreCase)
    List<Product> findByModelNameContainingIgnoreCase(String partialName);

    // Buscar productos de una categoría específica siempre y cuando estén activos
    List<Product> findByCategory_IdAndIsActiveTrue(Integer categoryId);
}