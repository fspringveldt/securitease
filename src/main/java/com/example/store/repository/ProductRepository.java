package com.example.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.store.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
