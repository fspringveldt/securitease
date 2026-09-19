package com.example.store.mapper;

import com.example.store.dto.CreateCustomerRequest;
import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;

import org.mapstruct.Mapper;
import org.springframework.lang.NonNull;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDto(Customer customer);

    List<CustomerDTO> toDtoList(List<Customer> customer);

    @NonNull Customer toEntity(@NonNull CreateCustomerRequest request);
}
