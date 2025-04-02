package com.example.accessingdatajpa;

import com.example.accessingdatajpa.entity.Product;
import com.example.accessingdatajpa.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.math.BigDecimal;

@EnableJpaAuditing
@SpringBootApplication
public class AccessingDataJpaApplication {

	private static final Logger log = LoggerFactory.getLogger(AccessingDataJpaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AccessingDataJpaApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(ProductRepository productRepository) {

		return args -> {

			productRepository.save(new Product("Laptop", "Cool", BigDecimal.valueOf(1599.99)));
			productRepository.save(new Product("Smartphone", "Great", BigDecimal.valueOf(999.99)));
			productRepository.save(new Product("Headphones", "Nice", BigDecimal.valueOf(249.99)));

			log.info("Products found with findAll():");
			log.info("-----------------------------");
			productRepository.findAll().forEach(product -> {
				log.info(product.toString());
			});
			log.info("");

			Product product = productRepository.findById(1L).orElseThrow();
			log.info("Product found with findById(1L):");
			log.info("--------------------------------");
			log.info(product.toString());
			log.info("");

			log.info("Products found with findByName('Laptop'):");
			log.info("----------------------------------------");
			productRepository.findByName("Laptop").forEach(laptop -> {
				log.info(laptop.toString());
			});
			log.info("");

			log.info("Product found with findByPrice('249.99'):");
			log.info("------------------------------------------");
			productRepository.findByPrice(BigDecimal.valueOf(249.99))
					.forEach(p -> log.info(p.toString()));
			log.info("");
		};
	}

}
