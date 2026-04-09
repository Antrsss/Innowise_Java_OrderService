package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.dao.OrderDao;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.exception.EntityNotFoundException;
import com.innowise.orderservice.exception.ResourceConflictException;
import com.innowise.orderservice.specification.OrderSpecification;
import com.innowise.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderDao orderDao;
  private final UserClient userClient;

  @Override
  @Transactional
  public Order createOrder(Order order) throws ResourceConflictException, EntityNotFoundException {
    if (order.getId() != null && orderDao.findById(order.getId()).isPresent()) {
      throw new ResourceConflictException("Order already exists");
    }

    if (order.getUserId() == null || userClient.findUserById(order.getUserId()).isEmpty()) {
      throw new EntityNotFoundException("User for this order not found");
    }

    if (order.getItems() != null) {
      order.getItems().forEach(item -> item.setOrder(order));
    }

    return orderDao.save(order);
  }

  @Override
  public Order findOrderById(Long id) throws EntityNotFoundException {
    return orderDao.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new EntityNotFoundException("Order not found"));
  }

  @Override
  public List<Order> findOrdersByUserId(Long id) {
    return orderDao.findAllByUserIdAndDeletedFalse(id);
  }

  @Override
  public Page<Order> findAll(Long userId, Collection<String> status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
    Specification<Order> specification = Specification.allOf(
        OrderSpecification.isNotDeleted(),
        OrderSpecification.hasUserId(userId),
        OrderSpecification.hasStatuses(status),
        OrderSpecification.createdBetween(startDate, endDate)
    );

    return orderDao.findAll(specification, pageable);
  }

  @Override
  @Transactional
  public Order updateOrder(Order order) throws EntityNotFoundException {
    Order existingOrder = findOrderById(order.getId());

    if (order.getStatus() != null) {
      existingOrder.setStatus(order.getStatus());
    }
    if (order.getTotalPrice() != null) {
      existingOrder.setTotalPrice(order.getTotalPrice());
    }

    return orderDao.save(existingOrder);
  }

  @Override
  @Transactional
  public void deleteOrder(Long id) throws EntityNotFoundException {
    if (orderDao.findById(id).isEmpty()) {
      throw new EntityNotFoundException("Order not found");
    }

    orderDao.softDeleteById(id);
  }
}

