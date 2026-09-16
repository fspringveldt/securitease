package com.example.store.controller;

import com.example.store.dto.CreateOrderRequest;
import com.example.store.dto.OrderCustomerDTO;
import com.example.store.dto.OrderDTO;
import com.example.store.dto.OrderProductDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.mapper.CustomerMapper;
import com.example.store.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ComponentScan(basePackageClasses = CustomerMapper.class)
@RequiredArgsConstructor
class OrderControllerTests extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private Order order;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setName("John Doe");
        customer.setId(1L);

        order = new Order();
        order.setDescription("Test Order");
        order.setId(1L);
        order.setCustomer(customer);
    }

    @Test
    void testCreateOrder() throws Exception {
        OrderDTO orderDTO = orderDTO();
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderDTO);

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Test Order"))
                .andExpect(jsonPath("$.customer.name").value("John Doe"))
                .andExpect(jsonPath("$.products[0].description").value("Test Product"));
    }

    @Test
    void testGetOrder() throws Exception {
        when(orderService.getAllOrders(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(orderDTO())));

        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Test Order"))
                .andExpect(jsonPath("$..customer.name").value("John Doe"))
                .andExpect(jsonPath("$.content[0].products[0].id").value(2));
    }

    // Add a test for for single order
    @Test
    void testGetOrderById() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(orderDTO());

        mockMvc.perform(get("/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test Order"))
                .andExpect(jsonPath("$.customer.name").value("John Doe"))
                .andExpect(jsonPath("$.products[0].description").value("Test Product"));
    }

    // Test 404
    @Test
    void testGetOrderByIdNotFound() throws Exception {
        when(orderService.getOrderById(1L))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/order/1")).andExpect(status().isNotFound());
    }

    private OrderDTO orderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setDescription("Test Order");

        OrderCustomerDTO customerDTO = new OrderCustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("John Doe");
        orderDTO.setCustomer(customerDTO);

        OrderProductDTO productDTO = new OrderProductDTO();
        productDTO.setId(2L);
        productDTO.setDescription("Test Product");
        orderDTO.setProducts(List.of(productDTO));

        return orderDTO;
    }

    private CreateOrderRequest createOrderRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setDescription("Test Order");
        request.setCustomerId(1L);
        request.setProductIds(List.of(2L));
        return request;
    }
}
