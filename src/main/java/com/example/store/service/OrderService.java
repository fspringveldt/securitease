package com.example.store.service;

import com.example.store.dto.CreateOrderRequest;
import com.example.store.dto.OrderDTO;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class OrderService {
    private final String cacheName = "orders";
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Cacheable(sync = true, value = cacheName, key = "'all-' + #pageable")
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(@NonNull Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    @Cacheable(sync = true, value = cacheName, key = "#id")
    public OrderDTO getOrderById(@NonNull Long id) {
        return orderMapper.toDto(
                orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @CacheEvict(value = cacheName, allEntries = true)
    @Transactional
    public OrderDTO createOrder(@NonNull CreateOrderRequest request) {
        if (!customerRepository.existsById(request.getCustomerId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }

        List<Product> products = productRepository.findAllById(request.getProductIds());
        Set<Long> foundProductIds = products.stream().map(Product::getId).collect(Collectors.toSet());
        if (!foundProductIds.containsAll(request.getProductIds())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more products not found");
        }

        Order order = orderMapper.toEntity(request);
        order.setCustomer(customerRepository.getReferenceById(request.getCustomerId()));
        products.forEach(order::addProduct);

        return orderMapper.toDto(orderRepository.save(order));
    }
}
