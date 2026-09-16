package com.example.store.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class ProductService {
    private final String cacheName = "products";
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Cacheable(value = cacheName, key = "'all-' + #pageable")
    public Page<ProductDTO> getAllProducts(@NonNull Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::productToProductDTO);
    }

    @Cacheable(value = cacheName, key = "#id")
    public ProductDTO getProductById(@NonNull Long id) {
        return productMapper.productToProductDTO(
                productRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @CacheEvict(value = cacheName, allEntries = true)
    public ProductDTO createProduct(@NonNull Product product) {
        return productMapper.productToProductDTO(productRepository.save(product));
    }
}
