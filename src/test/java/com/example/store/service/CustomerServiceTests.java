package com.example.store.service;

import com.example.store.dto.CreateCustomerRequest;
import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTests {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getAllCustomersReturnsMappedPage() {
        Pageable pageable = PageRequest.of(1, 20);
        Customer customer = customer("John Doe");
        CustomerDTO customerDTO = customerDTO("John Doe");
        Page<Customer> customers = new PageImpl<>(List.of(customer), pageable, 41);

        when(customerRepository.findAll(pageable)).thenReturn(customers);
        when(customerMapper.toDto(customer)).thenReturn(customerDTO);

        Page<CustomerDTO> result = customerService.getAllCustomers(pageable);

        assertEquals(List.of(customerDTO), result.getContent());
        assertEquals(1, result.getNumber());
        assertEquals(20, result.getSize());
        assertEquals(41, result.getTotalElements());
        verify(customerRepository).findAll(pageable);
        verify(customerMapper).toDto(customer);
    }

    @Test
    void getCustomersByNamePartReturnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 20);
        Customer customer = customer("Lance Stiedemann Sr.");
        CustomerDTO customerDTO = customerDTO("Lance Stiedemann Sr.");
        Page<Customer> customers = new PageImpl<>(List.of(customer), pageable, 1);

        when(customerRepository.findCustomersByNamePart("Stiedeman", pageable)).thenReturn(customers);
        when(customerMapper.toDto(customer)).thenReturn(customerDTO);

        Page<CustomerDTO> result = customerService.getCustomersByNamePart("Stiedeman", pageable);

        assertEquals(List.of(customerDTO), result.getContent());
        assertEquals(1, result.getTotalElements());
        verify(customerRepository).findCustomersByNamePart("Stiedeman", pageable);
        verify(customerMapper).toDto(customer);
    }

    @Test
    void getCustomersByNamePartRejectsQueriesContainingWhitespace() {
        Pageable pageable = PageRequest.of(0, 20);

        Page<CustomerDTO> result = customerService.getCustomersByNamePart("John Doe", pageable);

        assertEquals(0, result.getTotalElements());
        verifyNoInteractions(customerRepository, customerMapper);
    }

    @Test
    void createCustomerSavesAndMapsCustomer() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("John Doe");
        Customer customer = customer("John Doe");
        CustomerDTO customerDTO = customerDTO("John Doe");

        when(customerMapper.toEntity(request)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toDto(customer)).thenReturn(customerDTO);

        CustomerDTO result = customerService.createCustomer(request);

        assertEquals(customerDTO, result);
        verify(customerRepository).save(customer);
        verify(customerMapper).toDto(customer);
    }

    private Customer customer(String name) {
        Customer customer = new Customer();
        customer.setName(name);
        return customer;
    }

    private CustomerDTO customerDTO(String name) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName(name);
        return customerDTO;
    }
}
