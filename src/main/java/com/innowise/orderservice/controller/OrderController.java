package com.innowise.orderservice.controller;

import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.dto.UserDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.exception.EntityNotFoundException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private static final String USER_EMAIL_HEADER = "X-User-Email";

  private final OrderService orderService;
  private final OrderMapper orderMapper;
  private final UserClient userClient;

  @PostMapping
  public ResponseEntity<OrderResponseDto> createOrder(
      @Valid @RequestBody OrderDto orderDto,
      @RequestHeader(USER_EMAIL_HEADER) String email
  ) {
    UserDto userInfo = userClient.findUserByEmail(email);
    if (userInfo == null) {
      throw new EntityNotFoundException("User for this order not found");
    }

    Order orderEntity = orderMapper.toEntity(orderDto);
    orderEntity.setUserId(userInfo.getId());
    Order order = orderService.createOrder(orderEntity);

    OrderResponseDto response = new OrderResponseDto(orderMapper.toDto(order), userInfo);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponseDto> getOrderById(
      @PathVariable Long id,
      @RequestHeader(USER_EMAIL_HEADER) String email
  ) {
    Order order = orderService.findOrderById(id);
    UserDto userInfo = userClient.findUserByEmail(email);

    OrderResponseDto response = new OrderResponseDto(orderMapper.toDto(order), userInfo);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(
      @PathVariable Long userId,
      @RequestHeader(USER_EMAIL_HEADER) String email
  ) {
    List<Order> orders = orderService.findOrdersByUserId(userId);
    UserDto userInfo = userClient.findUserByEmail(email);

    List<OrderResponseDto> response = orders.stream()
        .map(o -> new OrderResponseDto(orderMapper.toDto(o), userInfo))
        .toList();

    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<Page<OrderResponseDto>> getAllOrders(
      @RequestParam(required = false) List<String> status,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
      Pageable pageable,
      @RequestHeader(USER_EMAIL_HEADER) String email
  ) {
    UserDto userInfo = userClient.findUserByEmail(email);
    Page<Order> ordersPage = orderService.findAll(userInfo.getId(), status, startDate, endDate, pageable);

    Page<OrderResponseDto> response = ordersPage.map(o ->
        new OrderResponseDto(orderMapper.toDto(o), userInfo));

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<OrderResponseDto> updateOrder(
      @PathVariable Long id,
      @Valid @RequestBody OrderDto orderDto,
      @RequestHeader(USER_EMAIL_HEADER) String email
  ) {
    Order orderEntity = orderMapper.toEntity(orderDto);
    orderEntity.setId(id);

    Order updatedOrder = orderService.updateOrder(orderEntity);
    UserDto userInfo = userClient.findUserByEmail(email);

    OrderResponseDto response = new OrderResponseDto(orderMapper.toDto(updatedOrder), userInfo);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}

