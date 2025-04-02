package com.example.accessingdatajpa.controller;

import com.example.accessingdatajpa.entity.Product;
import com.example.accessingdatajpa.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.empty;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        productRepository.save(new Product("Laptop Dell", "Cool", BigDecimal.valueOf(1500)));
        productRepository.save(new Product("MacBook Pro", "Great", BigDecimal.valueOf(2500)));
        productRepository.save(new Product("Dell Monitor", "Nice", BigDecimal.valueOf(500)));
    }

    @Test
    void searchProducts_shouldReturnFilteredAndSortedResults() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                        .param("name", "dell")
                        .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Dell Monitor")))
                .andExpect(jsonPath("$[0].price", is(500.0)))
                .andExpect(jsonPath("$[1].name", is("Laptop Dell")))
                .andExpect(jsonPath("$[1].price", is(1500.0)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                        .param("name", "dell")
                        .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price", is(1500.0)))
                .andExpect(jsonPath("$[1].price", is(500.0)));
    }

    @Test
    void searchProducts_withEmptyName_shouldReturnAllSorted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                        .param("name", "")
                        .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].price", is(500.0)))
                .andExpect(jsonPath("$[1].price", is(1500.0)))
                .andExpect(jsonPath("$[2].price", is(2500.0)));
    }

    @Test
    void searchProducts_noMatches_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                        .param("name", "nonexistent")
                        .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }
}