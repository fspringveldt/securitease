package com.example.store.service;

import com.example.store.config.KafkaTopic;
import com.example.store.dto.CreateCustomerRequest;
import com.example.store.dto.CustomerDTO;
import com.example.store.dto.UpdateCustomerRequest;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

@Service
@Validated
@RequiredArgsConstructor
public class CustomerService {
    private final String byIdCacheName = "customers-by-id";
    private final String pagesCacheName = "customers-pages";
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private static final Supplier<ResponseStatusException> CUSTOMER_NOT_FOUND =
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");

    @Cacheable(sync = true, value = pagesCacheName, key = "'all-' + #pageable")
    @Transactional(readOnly = true)
    public Page<CustomerDTO> getAllCustomers(@NonNull Pageable pageable) {
        return customerRepository.findAll(pageable).map(customerMapper::toDto);
    }

    @Cacheable(sync = true, value = byIdCacheName, key = "#id")
    public CustomerDTO getOneCustomer(@NonNull Long id) {
        return customerMapper.toDto(customerRepository.findById(id).orElseThrow(CUSTOMER_NOT_FOUND));
    }

    @Caching(
            evict = {
                @CacheEvict(value = byIdCacheName, key = "#id"),
                @CacheEvict(value = pagesCacheName, allEntries = true)
            })
    @Transactional
    public CustomerDTO updateCustomer(@NonNull Long id, @NonNull UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id).orElseThrow(CUSTOMER_NOT_FOUND);
        customer.setName(request.getName());
        customerRepository.save(customer);
        return customerMapper.toDto(customer);
    }

    @Cacheable(sync = true, value = pagesCacheName, key = "#namePart + '-' + #pageable")
    @Transactional(readOnly = true)
    public Page<CustomerDTO> getCustomersByNamePart(@NonNull String namePart, @NonNull Pageable pageable) {
        if (namePart.isBlank() || namePart.chars().anyMatch(Character::isWhitespace)) {
            return Page.empty(pageable);
        }

        return customerRepository.findCustomersByNamePart(namePart, pageable).map(customerMapper::toDto);
    }

    @CacheEvict(value = pagesCacheName, allEntries = true)
    public CustomerDTO createCustomer(@NonNull CreateCustomerRequest request) {
        return customerMapper.toDto(customerRepository.save(customerMapper.toEntity(request)));
    }

    public void sendCreateCustomerEvent(CreateCustomerRequest customer) {
        logger.info("MOOO: {}", customer);
        kafkaTemplate.send(KafkaTopic.CUSTOMERS, customer);
    }

    @KafkaListener(topics = KafkaTopic.CUSTOMERS, groupId = KafkaTopic.GROUP_NAME)
    public void listenCreateCustomer(@NonNull CreateCustomerRequest customer) {
        logger.info("FOO");
        logger.info("Customer create request: {}", customer);

        if (customer != null) {
            var created = createCustomer(customer);
            logger.info("Customer created {}", created);
        }
    }

    @Caching(
            evict = {
                @CacheEvict(value = byIdCacheName, key = "#id"),
                @CacheEvict(value = pagesCacheName, allEntries = true)
            })
    public void deleteCustomer(@NonNull Long id) {
        if (!customerRepository.existsById(id)) {
            throw CUSTOMER_NOT_FOUND.get();
        }
        customerRepository.deleteById(id);
    }
}
