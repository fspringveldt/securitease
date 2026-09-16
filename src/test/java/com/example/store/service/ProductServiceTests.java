package com.example.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllProductsReturnsMappedPage() {
        Pageable pageable = PageRequest.of(1, 20);
        Product product = product("Test Product");
        ProductDTO productDTO = productDTO("Test Product");
        Page<Product> products = new PageImpl<>(List.of(product), pageable, 41);

        when(productRepository.findAll(pageable)).thenReturn(products);
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        Page<ProductDTO> result = productService.getAllProducts(pageable);

        assertEquals(List.of(productDTO), result.getContent());
        assertEquals(1, result.getNumber());
        assertEquals(20, result.getSize());
        assertEquals(41, result.getTotalElements());
        verify(productRepository).findAll(pageable);
        verify(productMapper).productToProductDTO(product);
    }

    @Test
    void getProductByIdReturnsMappedProduct() {
        Product product = product("Test Product");
        ProductDTO productDTO = productDTO("Test Product");
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.getProductById(1L);

        assertEquals(productDTO, result);
        verify(productRepository).findById(1L);
        verify(productMapper).productToProductDTO(product);
    }

    @Test
    void getProductByIdThrowsNotFoundWhenProductDoesNotExist() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> productService.getProductById(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(productRepository).findById(1L);
    }

    @Test
    void createProductSavesAndMapsProduct() {
        Product product = product("Test Product");
        ProductDTO productDTO = productDTO("Test Product");

        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.createProduct(product);

        assertEquals(productDTO, result);
        verify(productRepository).save(product);
        verify(productMapper).productToProductDTO(product);
    }

    private Product product(String description) {
        Product product = new Product();
        product.setDescription(description);
        return product;
    }

    private ProductDTO productDTO(String description) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setDescription(description);
        return productDTO;
    }
}
