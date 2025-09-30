package com.ecom2.repo;

import com.ecom2.entity.Product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> { 
	List<Product> findByActiveTrue();
	List<Product> findByActiveFalse();
	Optional<Product> findByIdAndActiveTrue(Long id);
}
