package com.shinra.hardware.service.impl;

import com.shinra.hardware.dto.DiscoveredProductDTO;
import com.shinra.hardware.model.*;
import com.shinra.hardware.repository.*;
import com.shinra.hardware.service.ProductService;
import com.shinra.hardware.service.StoreListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
        return productRepository.findByModelNameContainingIgnoreCase(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Double findBestPriceForProduct(Long productId) {
        return storeListingRepository.findMinPriceByProductId(productId)
                .map(java.math.BigDecimal::doubleValue)
                .orElse(null);
    }

    // ----------------------------------------------------------------
    // LÓGICA DE IMPORTACIÓN
    // ----------------------------------------------------------------

    @Override
    public int importDiscoveredProducts(List<DiscoveredProductDTO> products) {
        int importedCount = 0;

        for (DiscoveredProductDTO dto : products) {
            try {
                processSingleProductImport(dto);
                importedCount++;
            } catch (Exception e) {
                System.err.println("Error procesando item '" + dto.title() + "': " + e.getMessage());
            }
        }
        return importedCount;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processSingleProductImport(DiscoveredProductDTO dto) {
        Store store = resolveOrCreateStore(dto);

        Product product = productRepository.findByModelName(dto.title())
                .orElseGet(() -> createNewProductFromDTO(dto));

        Optional<StoreListing> existingListing = storeListingRepository
                .findByProduct_IdAndStore_Id(product.getId(), store.getId());

        StoreListing listing;

        if (existingListing.isPresent()) {
            listing = existingListing.get();
            listing.setCurrentPrice(dto.price());
            listing.setIsInStock(true);
            listing.setLastCheckedAt(OffsetDateTime.now());

            if (!listing.getUrlSource().equals(dto.url())) {
                listing.setUrlSource(dto.url());
            }
        } else {
            listing = new StoreListing();
            listing.setProduct(product);
            listing.setStore(store);
            listing.setUrlSource(dto.url());
            listing.setCurrentPrice(dto.price());
            listing.setIsInStock(true);
            listing.setLastCheckedAt(OffsetDateTime.now());
        }

        storeListingService.saveListing(listing);
    }

    // --- MÉTODOS AUXILIARES ---

    private Product createNewProductFromDTO(DiscoveredProductDTO dto) {
        Product newProduct = new Product();
        newProduct.setModelName(dto.title());
        newProduct.setBrand(detectBrand(dto.title()));
        newProduct.setImageUrl(dto.imageUrl());
        newProduct.setIsActive(true);

        Category category = categoryRepository.findBySlug(dto.categorySlug())
                .orElseGet(() -> createNewCategory(dto.categorySlug()));

        newProduct.setCategory(category);
        newProduct.setTechSpecs(Map.of("source", "auto_scraped", "imported_at", OffsetDateTime.now().toString()));

        return productRepository.save(newProduct);
    }

    private Category createNewCategory(String slug) {
        Category newCat = new Category();
        newCat.setSlug(slug);
        String name = slug.substring(0, 1).toUpperCase() + slug.substring(1).toLowerCase();
        newCat.setName(name);
        return categoryRepository.save(newCat);
    }

    private Store resolveOrCreateStore(DiscoveredProductDTO dto) {
        String lowerUrl = dto.url().toLowerCase();

        // 1. Intentar buscar por palabra clave en URL (ej: "amazon")
        String domainKeyword = extractDomainKeyword(lowerUrl);
        Store foundStore = getStoreByKeyword(domainKeyword);

        if (foundStore != null) return foundStore;

        // 2. 🔥 NUEVO: Si falla por URL, buscar por NOMBRE exacto (ej: "Amazon")
        // Esto evita el error de llave duplicada si la tienda ya existe con ese nombre.
        String storeNameTarget = dto.storeName() != null ? dto.storeName() : "Tienda (" + domainKeyword + ")";
        Optional<Store> storeByName = storeRepository.findByName(storeNameTarget);

        if (storeByName.isPresent()) {
            return storeByName.get();
        }

        // 3. Si no existe ni por URL ni por Nombre, CREARLA
        Store newStore = new Store();
        newStore.setName(storeNameTarget);
        newStore.setBaseUrl(extractBaseUrl(dto.url()));
        newStore.setLogoUrl("");
        newStore.setScrapeFrequencyHours(24);

        return storeRepository.save(newStore);
    }

    private Store getStoreByKeyword(String keyword) {
        if (keyword.equals("unknown")) return null;
        return storeRepository.findByBaseUrlContaining(keyword)
                .stream().findFirst().orElse(null);
    }

    private String extractDomainKeyword(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            String host = uri.getHost();
            if (host != null) {
                String[] parts = host.split("\\.");
                if (parts.length >= 2) {
                    if (parts[parts.length - 2].equals("com") && parts.length > 2) {
                        return parts[parts.length - 3];
                    }
                    return parts[parts.length - 2];
                }
            }
        } catch (Exception e) {}
        return "unknown";
    }

    private String extractBaseUrl(String productUrl) {
        try {
            java.net.URI uri = new java.net.URI(productUrl);
            return uri.getScheme() + "://" + uri.getHost();
        } catch (Exception e) {
            return productUrl;
        }
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
        if (upper.contains("KINGSTON")) return "Kingston";
        if (upper.contains("CORSAIR")) return "Corsair";
        if (upper.contains("LOGITECH")) return "Logitech";
        if (upper.contains("RAZER")) return "Razer";
        if (upper.contains("SAMSUNG")) return "Samsung";
        return "Genérico";
    }
}