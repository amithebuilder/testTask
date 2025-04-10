package com.example.accessingdatajpa.controller;

import com.example.accessingdatajpa.entity.Product;
import com.example.accessingdatajpa.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        if (!List.of("asc", "desc").contains(sortDirection.toLowerCase())) {
            sortDirection = "asc"; //дефолтное значение типа
        }

        Sort sort = Sort.by(
                sortDirection.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                "price"
        );

        List<Product> products = productRepository.findByNameContainingIgnoreCase(name, sort);

        return ResponseEntity.ok(products);
    }
}