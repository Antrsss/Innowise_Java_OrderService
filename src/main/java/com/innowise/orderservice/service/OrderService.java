package com.innowise.orderservice.service;

import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.exception.EntityNotFoundException;
import com.innowise.orderservice.exception.ResourceConflictException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface OrderService {
  Order createOrder(Order order) throws ResourceConflictException, EntityNotFoundException;
  Order findOrderById(Long id) throws EntityNotFoundException;
  List<Order> findOrdersByUserId(Long id);
  Page<Order> findAll(Long userId, Collection<String> status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
  Order updateOrder(Order order) throws EntityNotFoundException;
  void deleteOrder(Long id) throws EntityNotFoundException;
}
