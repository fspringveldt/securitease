package com.example.store.service;

import com.example.store.dto.CreateOrderRequest;
import com.example.store.dto.OrderDTO;
import com.example.store.dto.OrderProductDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getAllOrdersReturnsMappedPage() {
        Pageable pageable = PageRequest.of(1, 20);
        Order order = order("Test Order");
        OrderDTO orderDTO = orderDTO("Test Order");
        Page<Order> orders = new PageImpl<>(List.of(order), pageable, 41);

        when(orderRepository.findAll(pageable)).thenReturn(orders);
        when(orderMapper.toDto(order)).thenReturn(orderDTO);

        Page<OrderDTO> result = orderService.getAllOrders(pageable);

        assertEquals(List.of(orderDTO), result.getContent());
        assertEquals(1, result.getNumber());
        assertEquals(20, result.getSize());
        assertEquals(41, result.getTotalElements());
        verify(orderRepository).findAll(pageable);
        verify(orderMapper).toDto(order);
    }

    @Test
    void getOrderByIdReturnsMappedOrder() {
        Order order = order("Test Order");
        OrderDTO orderDTO = orderDTO("Test Order");
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.getOrderById(1L);

        assertEquals(orderDTO, result);
        verify(orderRepository).findById(1L);
        verify(orderMapper).toDto(order);
    }

    @Test
    void getOrderByIdThrowsNotFoundWhenOrderDoesNotExist() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> orderService.getOrderById(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(orderRepository).findById(1L);
    }

    @Test
    void createOrderSavesAndMapsOrder() {
        OrderDTO orderDTO = orderDTO("Test Order");

        CreateOrderRequest request = createOrderRequest();
        Customer customer = new Customer();
        Product product = new Product();
        product.setId(2L);
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(customerRepository.getReferenceById(1L)).thenReturn(customer);
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(product));
        when(orderMapper.toEntity(request)).thenReturn(order(request.getDescription()));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(request);

        assertEquals(orderDTO, result);
        verify(orderRepository)
                .save(argThat(savedOrder -> savedOrder.getProducts().size() == 1
                        && savedOrder.getProducts().get(0).getProduct() == product));
        verify(orderMapper).toDto(any(Order.class));
    }

    private Order order(String description) {
        Order order = new Order();
        order.setDescription(description);
        return order;
    }

    private OrderDTO orderDTO(String description) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setDescription(description);
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
