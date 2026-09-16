package com.example.store.mapper;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.OrderProduct;
import com.example.store.entity.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "orderIds", source = "orders")
    ProductDTO toDto(Product product);

    List<ProductDTO> toEntityList(List<Product> products);

    default Long orderProductToOrderId(OrderProduct orderProduct) {
        return orderProduct.getOrder().getId();
    }
}
