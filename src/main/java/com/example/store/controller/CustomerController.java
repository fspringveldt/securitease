package com.example.store.controller;

import com.example.store.dto.CreateCustomerRequest;
import com.example.store.dto.CustomerDTO;
import com.example.store.dto.UpdateCustomerRequest;
import com.example.store.service.CustomerService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    @GetMapping("/test-thread")
    public String handleRequest() throws InterruptedException {
        Thread currentThread = Thread.currentThread();

        logger.info("Handing request on {}", currentThread);

        // Simulate a heavy -second IO-blocking process
        Thread.sleep(2000);
        return String.format("Executed on: %s", currentThread.getName());
    }

    @GetMapping
    public Page<CustomerDTO> getAllCustomers(@PageableDefault(size = 20) @NonNull Pageable pageable) {
        return customerService.getAllCustomers(pageable);
    }

    @GetMapping("/{id}")
    public CustomerDTO getOneCustomer(@NonNull @PathVariable Long id) {
        return customerService.getOneCustomer(id);
    }

    @PutMapping("/{id}")
    public CustomerDTO updateCustomer(
            @NonNull @PathVariable Long id, @NonNull @RequestBody UpdateCustomerRequest request) {
        return customerService.updateCustomer(id, request);
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

    @PostMapping("/create-customer-event")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendCreateCustomer(@RequestBody @NonNull CreateCustomerRequest request) {
        customerService.sendCreateCustomerEvent(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@NonNull @PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}
