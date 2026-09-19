package com.example.store.controller;

import com.example.store.dto.CreateCustomerRequest;
import com.example.store.dto.CustomerDTO;
import com.example.store.service.CustomerService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Page<CustomerDTO> getAllCustomers(@PageableDefault(size = 20) @NonNull Pageable pageable) {
        return customerService.getAllCustomers(pageable);
    }

    @GetMapping(params = "name")
    public Page<CustomerDTO> getCustomersByNamePart(
            @RequestParam("name") @NonNull String namePart, @PageableDefault(size = 20) @NonNull Pageable pageable) {
        return customerService.getCustomersByNamePart(namePart, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO createCustomer(@RequestBody @NonNull CreateCustomerRequest request) {
        return customerService.createCustomer(request);
    }
}
