package com.shinra.hardware.controller;

import com.shinra.hardware.dto.DiscoveredProductDTO;
import com.shinra.hardware.dto.ProductDTO;
import com.shinra.hardware.model.Product;
import com.shinra.hardware.model.StoreListing;
import com.shinra.hardware.service.ProductService;
import com.shinra.hardware.service.StoreListingService;
import com.shinra.hardware.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreListingService listingService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> dtos = productService.findAllProducts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.findProductById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(convertToDTO(savedProduct));
    }

    @PostMapping("/import")
    public ResponseEntity<String> importDiscoveredProducts(@RequestBody List<DiscoveredProductDTO> newProducts) {
        int count = 0;

        for (DiscoveredProductDTO dto : newProducts) {
            Product product = new Product();
            product.setModelName(dto.title());
            product.setBrand("Generico");
            product.setImageUrl(dto.imageUrl());
            productService.saveProduct(product);

            StoreListing listing = new StoreListing();
            listing.setProduct(product);
            listing.setUrlSource(dto.url());
            listing.setCurrentPrice(dto.price());
            listing.setIsInStock(true);

            listingService.saveListing(listing);
            count++;
        }

        return ResponseEntity.ok("Importados " + count + " productos.");
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String q) {
        List<ProductDTO> dtos = productService.searchProductsByName(q).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getByCategory(@PathVariable Integer categoryId) {
        List<ProductDTO> dtos = productService.findActiveProductsByCategoryId(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // -----------------------------------------------------------
    // MAPPING ENTITY -> DTO
    // -----------------------------------------------------------
    private ProductDTO convertToDTO(Product product) {

        Double bestPrice = productService.findBestPriceForProduct(product.getId());

        String categoryName = (product.getCategory() != null)
                ? product.getCategory().getName()
                : "Sin Categoría";

        return new ProductDTO(
                product.getId(),
                categoryName,
                product.getModelName(),
                product.getBrand(),
                product.getTechSpecs(),
                product.getImageUrl(),
                bestPrice
        );
    }
}
