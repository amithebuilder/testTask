package com.example.accessingdatajpa.repository;

import com.example.accessingdatajpa.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product savedLaptop;
    private Product savedSmartphone;
    private Product savedSmartphonePro;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        savedLaptop = productRepository.save(
                new Product("Laptop", "Cool", BigDecimal.valueOf(1500.00))
        );

        savedSmartphone = productRepository.save(
                new Product("Smartphone", "Great", BigDecimal.valueOf(999.99))
        );

        savedSmartphonePro = productRepository.save(
                new Product("Smartphone Pro", "Nice", BigDecimal.valueOf(1299.99))
        );
    }

    @Test
    void findByName_shouldReturnExactMatch() {
        List<Product> found = productRepository.findByName("Laptop");
        assertThat(found).hasSize(1)
                .first()
                .extracting(Product::getName, Product::getPrice)
                .containsExactly("Laptop", BigDecimal.valueOf(1500.00));
    }

    @Test
    void findByName_shouldReturnEmptyWhenNotFound() {
        assertThat(productRepository.findByName("Nonexistent")).isEmpty();
    }

    @Test
    void findByPrice_shouldReturnProductsWithExactPrice() {
        List<Product> found = productRepository.findByPrice(BigDecimal.valueOf(999.99));
        assertThat(found).hasSize(1)
                .first()
                .extracting(Product::getName)
                .isEqualTo("Smartphone");
    }

    @Test
    void findByPrice_shouldBePreciseWithDecimals() {
        assertThat(productRepository.findByPrice(BigDecimal.valueOf(999.989))).isEmpty();
    }

    @Test
    void findByNameContaining_shouldFindPartialMatches() {
        List<Product> found = productRepository.findByNameContainingIgnoreCase(
                "smart", Sort.unsorted());
        assertThat(found)
                .hasSize(2)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Smartphone", "Smartphone Pro");
    }

    @Test
    void findByNameContaining_shouldBeCaseInsensitive() {
        List<Product> found = productRepository.findByNameContainingIgnoreCase(
                "LAPTOP", Sort.unsorted());
        assertThat(found)
                .hasSize(1)
                .first()
                .extracting(Product::getName)
                .isEqualTo("Laptop");
    }

    @Test
    void findByNameContaining_withSortAsc_shouldSortByPrice() {
        List<Product> found = productRepository.findByNameContainingIgnoreCase(
                "smart", Sort.by(Sort.Direction.ASC, "price"));
        assertThat(found)
                .hasSize(2)
                .extracting(Product::getName)
                .containsExactly("Smartphone", "Smartphone Pro");
    }

    @Test
    void findByNameContaining_withSortDesc_shouldSortByPrice() {
        List<Product> found = productRepository.findByNameContainingIgnoreCase(
                "smart", Sort.by(Sort.Direction.DESC, "price"));
        assertThat(found)
                .hasSize(2)
                .extracting(Product::getName)
                .containsExactly("Smartphone Pro", "Smartphone");
    }

    @Test
    void findByNameContaining_withEmptyString_shouldReturnAll() {
        List<Product> found = productRepository.findByNameContainingIgnoreCase(
                "", Sort.by(Sort.Direction.ASC, "name"));
        assertThat(found)
                .hasSize(3)
                .extracting(Product::getName)
                .containsExactly("Laptop", "Smartphone", "Smartphone Pro");
    }

    @Test
    void findByNameContaining_withNonexistent_shouldReturnEmpty() {
        assertThat(productRepository.findByNameContainingIgnoreCase(
                "xyz", Sort.unsorted())).isEmpty();
    }

    @Test
    void count_shouldReturnTotalProducts() {
        assertThat(productRepository.count()).isEqualTo(3);
    }

    @Test
    void findAll_shouldReturnAll() {
        assertThat(productRepository.findAll()).hasSize(3);
    }

    @Test
    void deleteById_shouldRemoveProduct() {
        productRepository.deleteById(savedLaptop.getId());
        assertThat(productRepository.count()).isEqualTo(2);
        assertThat(productRepository.findById(savedLaptop.getId())).isEmpty();
    }

    @Test
    void findById_shouldReturnProduct() {
        assertThat(productRepository.findById(savedSmartphone.getId()))
                .isPresent()
                .get()
                .extracting(Product::getName)
                .isEqualTo("Smartphone");
    }

    @Test
    void findById_shouldReturnEmptyForNonExisting() {
        assertThat(productRepository.findById(999L)).isEmpty();
    }
}