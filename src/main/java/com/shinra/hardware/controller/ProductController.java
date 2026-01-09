package com.shinra.hardware.controller;

import com.shinra.hardware.dto.ProductDTO;
import com.shinra.hardware.model.Product;
import com.shinra.hardware.service.ProductService;
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
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 1. Obtener todos (Mapeados a DTO)
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> dtos = productService.findAllProducts().stream()
                .map(this::convertToDTO) // Convertimos cada entidad a DTO
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // 2. Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.findProductById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Crear
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product) {
        // Nota: Recibimos Entity, devolvemos DTO
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(convertToDTO(savedProduct));
    }

    // 4. Búsqueda por Keyword (Para tu barra de búsqueda)
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String q) {
        List<ProductDTO> dtos = productService.searchProductsByName(q).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // 5. Filtrar por ID de Categoría
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
