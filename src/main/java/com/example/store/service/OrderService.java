package com.example.store.service;

import com.example.store.dto.OrderDTO;
import com.example.store.entity.Order;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.OrderRepository;

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

@Service
@RequiredArgsConstructor
@Validated
public class OrderService {
    private final String cacheName = "orders";
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Cacheable(value = cacheName, key = "'all-' + #pageable")
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(@NonNull Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    @Cacheable(value = cacheName, key = "#id")
    public OrderDTO getOrderById(@NonNull Long id) {
        // Ideally this should be handled by a service layer.
        // Left it like this for simplicity.
        return orderMapper.toDto(
                orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @CacheEvict(value = cacheName, allEntries = true)
    public OrderDTO createOrder(@NonNull Order order) {
        return orderMapper.toDto(orderRepository.save(order));
    }
}
