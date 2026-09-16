package com.example.store.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Data
@Table(name = "\"order\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> products = new ArrayList<>();

    // Helper to add product
    // While this join table isn't needed immediately as there's no additional info
    // being stored,
    // it's more future ready like this.
    public void addProduct(Product product) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(this);
        orderProduct.setProduct(product);
        ;
        this.products.add(orderProduct);
    }
}
