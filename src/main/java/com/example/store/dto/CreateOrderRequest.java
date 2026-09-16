package com.example.store.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateOrderRequest {
    private String description;

    @NonNull private Long customerId;

    @NonNull private List<Long> productIds;
}
