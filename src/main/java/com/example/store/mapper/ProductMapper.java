package com.example.store.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.OrderProduct;
import com.example.store.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "orderIds", source = "orders")
    ProductDTO productToProductDTO(Product product);

    List<ProductDTO> productsToProductDTOs(List<Product> products);

    default Long orderProductToOrderId(OrderProduct orderProduct) {
        return orderProduct.getOrder().getId();
    }
}
