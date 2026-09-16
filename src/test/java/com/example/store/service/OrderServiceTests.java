package com.example.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

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

import com.example.store.dto.OrderDTO;
import com.example.store.entity.Order;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

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
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        Page<OrderDTO> result = orderService.getAllOrders(pageable);

        assertEquals(List.of(orderDTO), result.getContent());
        assertEquals(1, result.getNumber());
        assertEquals(20, result.getSize());
        assertEquals(41, result.getTotalElements());
        verify(orderRepository).findAll(pageable);
        verify(orderMapper).orderToOrderDTO(order);
    }

    @Test
    void getOrderByIdReturnsMappedOrder() {
        Order order = order("Test Order");
        OrderDTO orderDTO = orderDTO("Test Order");
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.getOrderById(1L);

        assertEquals(orderDTO, result);
        verify(orderRepository).findById(1L);
        verify(orderMapper).orderToOrderDTO(order);
    }

    @Test
    void getOrderByIdThrowsNotFoundWhenOrderDoesNotExist() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> orderService.getOrderById(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(orderRepository).findById(1L);
    }

    @Test
    void createOrderSavesAndMapsOrder() {
        Order order = order("Test Order");
        OrderDTO orderDTO = orderDTO("Test Order");

        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(order);

        assertEquals(orderDTO, result);
        verify(orderRepository).save(order);
        verify(orderMapper).orderToOrderDTO(order);
    }

    private Order order(String description) {
        Order order = new Order();
        order.setDescription(description);
        return order;
    }

    private OrderDTO orderDTO(String description) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setDescription(description);
        return orderDTO;
    }
}
