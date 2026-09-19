package com.example.store.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class CreateCustomerRequest {
    @NonNull private String name;
}
