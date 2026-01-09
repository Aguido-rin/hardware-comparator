package com.shinra.hardware.service;

import com.shinra.hardware.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    //Crud
    List<Product> findAllProducts();
    Optional<Product> findProductById(Long id);
    Product saveProduct(Product product);
    Product updateProduct(Long id, Product product);

    void deleteProductById(Long id);

    List<Product> findActiveProductsByCategoryId(Integer categoryId);

    List<Product> findProductsByBrand(String brand);

    Double findBestPriceForProduct(Long productId);
    List<Product> searchProductsByName(String keyword);
}