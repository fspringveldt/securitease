package com.example.store.mapper;

import com.example.store.dto.CreateOrderRequest;
import com.example.store.dto.OrderCustomerDTO;
import com.example.store.dto.OrderDTO;
import com.example.store.dto.OrderProductDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.OrderProduct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.lang.NonNull;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "products", source = "products")
    OrderDTO toDto(Order order);

    List<OrderDTO> toDtoList(List<Order> orders);

    OrderCustomerDTO orderToOrderCustomerDTO(Customer customer);

    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "description", source = "product.description")
    OrderProductDTO toOrderProductDto(OrderProduct orderProduct);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "products", ignore = true)
    @NonNull Order toEntity(@NonNull CreateOrderRequest request);
}
