package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class CustomerService {
    private final String cacheName = "customers";
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Cacheable(value = cacheName, key = "'all-' + #pageable")
    public Page<CustomerDTO> getAllCustomers(@NonNull Pageable pageable) {
        return customerRepository.findAll(pageable).map(customerMapper::toDto);
    }

    @Cacheable(value = cacheName, key = "#namePart + '-' + #pageable")
    public Page<CustomerDTO> getCustomersByNamePart(@NonNull String namePart, @NonNull Pageable pageable) {
        return customerRepository.findCustomersByNamePart(namePart, pageable).map(customerMapper::toDto);
    }

    @CacheEvict(value = cacheName, allEntries = true)
    public CustomerDTO createCustomer(@NonNull Customer customer) {
        return customerMapper.toDto(customerRepository.save(customer));
    }
}
