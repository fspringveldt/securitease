package com.example.store.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.example.store.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	@Override
	@EntityGraph(attributePaths = {"customer", "products", "products.product"})
	@NonNull
	Page<Order> findAll(@NonNull Pageable pageable);

	@Override
	@EntityGraph(attributePaths = {"customer", "products", "products.product"})
	@NonNull
	Optional<Order> findById(@NonNull Long id);
}
