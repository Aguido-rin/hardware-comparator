package com.shinra.hardware.service.impl;

import com.shinra.hardware.model.Product;
import com.shinra.hardware.repository.ProductRepository;
import com.shinra.hardware.repository.StoreListingRepository;
import com.shinra.hardware.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StoreListingRepository storeListingRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              StoreListingRepository storeListingRepository) {
        this.productRepository = productRepository;
        this.storeListingRepository = storeListingRepository;
    }

    // ----------------------------------------------------------------
    // CRUD BÁSICO
    // ----------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id).map(existingProduct -> {

            existingProduct.setModelName(productDetails.getModelName());
            existingProduct.setBrand(productDetails.getBrand());
            existingProduct.setImageUrl(productDetails.getImageUrl());
            existingProduct.setIsActive(productDetails.getIsActive());

            existingProduct.setCategory(productDetails.getCategory());

            existingProduct.setTechSpecs(productDetails.getTechSpecs());

            return productRepository.save(existingProduct);

        }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. El producto no existe.");
        }
        productRepository.deleteById(id);
    }

    // ----------------------------------------------------------------
    // Consultas Repository Personalizadas
    // ----------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Product> findActiveProductsByCategoryId(Integer categoryId) {
        return productRepository.findByCategory_IdAndIsActiveTrue(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String keyword) {
        // Usa: findByModelNameContainingIgnoreCase
        return productRepository.findByModelNameContainingIgnoreCase(keyword);
    }

    // ----------------------------------------------------------------
    // LÓGICA DE PRECIOS
    // ----------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Double findBestPriceForProduct(Long productId) {
        return storeListingRepository.findMinPriceByProductId(productId)
                .map(java.math.BigDecimal::doubleValue)
                .orElse(null);
    }
}
