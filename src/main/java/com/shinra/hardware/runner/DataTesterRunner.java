package com.shinra.hardware.runner;

import com.shinra.hardware.dto.ProductDTO;
import com.shinra.hardware.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataTesterRunner implements CommandLineRunner {

    private final ProductService productService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=============================================");
        System.out.println(" INICIANDO TEST DE DATOS - HARDWARE COMPARATOR");
        System.out.println("=============================================");

        List<ProductDTO> deals = productService.getAllProductsWithBestDeals();

        if (deals.isEmpty()) {
            System.out.println("⚠️ No se encontraron productos.");
        } else {
            for (ProductDTO p : deals) {
                System.out.println("---------------------------------------------");
                System.out.println("PRODUCTO: " + p.brand() + " " + p.modelName());
                System.out.println("Categoria: " + p.categoryName());
                System.out.println("MEJOR PRECIO DETECTADO: $" + p.bestPrice());
                System.out.println("Specs: " + p.techSpecs());
            }
        }

        System.out.println("=============================================");
        System.out.println(" TEST FINALIZADO CON ÉXITO");
        System.out.println("=============================================");
    }
}