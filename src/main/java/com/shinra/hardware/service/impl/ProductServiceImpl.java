package com.shinra.hardware.service.impl;

import com.shinra.hardware.dto.DiscoveredProductDTO;
import com.shinra.hardware.model.*;
import com.shinra.hardware.repository.*;
import com.shinra.hardware.service.ProductService;
import com.shinra.hardware.service.StoreListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final StoreListingRepository storeListingRepository;
    private final StoreListingService storeListingService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              StoreRepository storeRepository,
                              StoreListingRepository listingRepository,
                              StoreListingService listingService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.storeRepository = storeRepository;
        this.storeListingRepository = listingRepository;
        this.storeListingService = listingService;
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

    @Override
    @Transactional
    public int importDiscoveredProducts(List<DiscoveredProductDTO> products) {
        int importedCount = 0;

        for (DiscoveredProductDTO dto : products) {
            try {
                if (storeListingRepository.existsByUrlSource(dto.url())) {
                    continue;
                }

                Store store = resolveStoreFromUrl(dto.url());
                if (store == null) {
                    System.err.println("❌ [IMPORT] Tienda no reconocida para URL: " + dto.url());
                    continue;
                }

                Product product = productRepository.findByModelName(dto.title())
                        .orElseGet(() -> createNewProductFromDTO(dto));

                StoreListing listing = new StoreListing();
                listing.setProduct(product);
                listing.setStore(store);
                listing.setUrlSource(dto.url());
                listing.setCurrentPrice(dto.price());
                listing.setIsInStock(true);
                listing.setLastCheckedAt(OffsetDateTime.now());

                storeListingService.saveListing(listing);

                importedCount++;

            } catch (Exception e) {

                System.err.println("❌ Error importando item '" + dto.title() + "': " + e.getMessage());
            }
        }
        return importedCount;
    }

    // --- MÉTODOS PRIVADOS AUXILIARES ---

    private Product createNewProductFromDTO(DiscoveredProductDTO dto) {
        Product newProduct = new Product();
        newProduct.setModelName(dto.title());
        newProduct.setBrand(detectBrand(dto.title()));
        newProduct.setImageUrl(dto.imageUrl());
        newProduct.setIsActive(true);

        Category category = categoryRepository.findBySlug(dto.categorySlug())
                .orElseThrow(() -> new RuntimeException("Categoría desconocida: " + dto.categorySlug()));
        newProduct.setCategory(category);

        newProduct.setTechSpecs(Map.of("source", "auto_scraped"));

        return productRepository.save(newProduct);
    }

    private Store resolveStoreFromUrl(String url) {
        String lowerUrl = url.toLowerCase();

        if (lowerUrl.contains("amazon")) return getStoreByKeyword("amazon");
        if (lowerUrl.contains("coolbox")) return getStoreByKeyword("coolbox");
        if (lowerUrl.contains("mercadolibre")) return getStoreByKeyword("mercadolibre");
        if (lowerUrl.contains("aliexpress")) return getStoreByKeyword("aliexpress");
        if (lowerUrl.contains("cyberpuerta")) return getStoreByKeyword("cyberpuerta");
        if (lowerUrl.contains("memorykings")) return getStoreByKeyword("memorykings");
        return null;
    }

    private Store getStoreByKeyword(String keyword) {

        return storeRepository.findByBaseUrlContaining(keyword)
                .stream().findFirst().orElse(null);
    }

    private String detectBrand(String title) {
        if (title == null) return "Genérico";
        String upper = title.toUpperCase();

        if (upper.contains("ASUS")) return "ASUS";
        if (upper.contains("MSI")) return "MSI";
        if (upper.contains("GIGABYTE")) return "Gigabyte";
        if (upper.contains("AMD") || upper.contains("RYZEN")) return "AMD";
        if (upper.contains("INTEL") || upper.contains("CORE")) return "Intel";
        if (upper.contains("NVIDIA") || upper.contains("GEFORCE") || upper.contains("RTX")) return "NVIDIA";
        if (upper.contains("KINGSTON") || upper.contains("FURY")) return "Kingston";
        if (upper.contains("CORSAIR")) return "Corsair";
        if (upper.contains("LOGITECH")) return "Logitech";
        if (upper.contains("RAZER")) return "Razer";
        if (upper.contains("HYPERX")) return "HyperX";
        if (upper.contains("SAMSUNG")) return "Samsung";

        return "Genérico";
    }
}
