package com.example.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import java.util.Optional;

import com.example.store.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	@Override
	@EntityGraph(attributePaths = {"orders", "orders.order"})
	@NonNull
	Page<Product> findAll(@NonNull Pageable pageable);

	@Override
	@EntityGraph(attributePaths = {"orders", "orders.order"})
	@NonNull
	Optional<Product> findById(@NonNull Long id);
}
