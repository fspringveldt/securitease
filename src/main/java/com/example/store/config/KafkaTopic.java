package com.example.store.config;

public final class KafkaTopic {
    public static final String ORDERS = "store-orders";
    public static final String CUSTOMERS = "store-customers";
    public static final String PRODUCTS = "store-products";
    public static final String GROUP_NAME = "store-group";
    public static final String DLT_SUFFIX = ".DLT";
    public static final String[] ALL_TOPICS = {ORDERS, CUSTOMERS, PRODUCTS};
}
