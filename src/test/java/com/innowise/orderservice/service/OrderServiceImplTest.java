package com.innowise.orderservice.service;

import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.dao.OrderDao;
import com.innowise.orderservice.dto.UserDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderItem;
import com.innowise.orderservice.exception.EntityNotFoundException;
import com.innowise.orderservice.exception.ResourceConflictException;
import com.innowise.orderservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

  @Mock
  private OrderDao orderDao;

  @Mock
  private UserClient userClient;

  @InjectMocks
  private OrderServiceImpl orderService;

  private Order testOrder;

  @BeforeEach
  void setUp() {
    testOrder = new Order();
    testOrder.setId(1L);
    testOrder.setUserId(100L);
    testOrder.setStatus("NEW");
  }

  @Test
  void createOrder_Success() throws ResourceConflictException, EntityNotFoundException {
    when(orderDao.findById(1L)).thenReturn(Optional.empty());
    when(userClient.findUserById(100L)).thenReturn(Optional.of(new UserDto()));
    when(orderDao.save(any(Order.class))).thenReturn(testOrder);

    Order result = orderService.createOrder(testOrder);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(orderDao, times(1)).save(testOrder);
  }

  @Test
  void createOrder_WithItems_Success() throws ResourceConflictException, EntityNotFoundException {
    OrderItem item = new OrderItem();
    testOrder.setItems(List.of(item));

    when(orderDao.findById(1L)).thenReturn(Optional.empty());
    when(userClient.findUserById(100L)).thenReturn(Optional.of(new UserDto()));
    when(orderDao.save(any(Order.class))).thenReturn(testOrder);

    Order result = orderService.createOrder(testOrder);

    assertNotNull(result.getItems());
    assertEquals(testOrder, item.getOrder());
    verify(orderDao).save(testOrder);
  }

  @Test
  void createOrder_ThrowsConflict_WhenOrderExists() {
    when(orderDao.findById(1L)).thenReturn(Optional.of(testOrder));

    assertThrows(ResourceConflictException.class, () -> orderService.createOrder(testOrder));
    verify(orderDao, never()).save(any());
  }

  @Test
  void createOrder_ThrowsNotFound_WhenUserDoesNotExist() {
    when(orderDao.findById(1L)).thenReturn(Optional.empty());
    when(userClient.findUserById(100L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(testOrder));
  }

  @Test
  void createOrder_ThrowsNotFound_WhenUserIdIsNull() {
    testOrder.setUserId(null);

    assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(testOrder));
    verify(userClient, never()).findUserById(any());
  }

  @Test
  void findOrderById_Success() throws EntityNotFoundException {
    when(orderDao.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testOrder));
    Order result = orderService.findOrderById(1L);

    assertEquals("NEW", result.getStatus());
  }

  @Test
  void findOrderById_ThrowsNotFound() {
    when(orderDao.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> orderService.findOrderById(1L));
  }

  @Test
  void findOrdersByUserId_Success() {
    List<Order> orders = List.of(testOrder);
    when(orderDao.findAllByUserIdAndDeletedFalse(100L)).thenReturn(orders);
    List<Order> result = orderService.findOrdersByUserId(100L);

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(orderDao).findAllByUserIdAndDeletedFalse(100L);
  }

  @Test
  void findAll_Success() {
    Page<Order> page = new PageImpl<>(List.of(testOrder));
    when(orderDao.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
    Page<Order> result = orderService.findAll("NEW", null, null, Pageable.unpaged());

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(orderDao).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void updateOrder_Success() throws EntityNotFoundException {
    Order updateData = new Order();
    updateData.setId(1L);
    updateData.setStatus("PAID");
    updateData.setTotalPrice(99.99);

    when(orderDao.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testOrder));
    when(orderDao.save(any(Order.class))).thenReturn(testOrder);
    Order result = orderService.updateOrder(updateData);

    assertEquals("PAID", result.getStatus());
    assertEquals(99.99, result.getTotalPrice());
  }

  @Test
  void updateOrder_WithPartialFields_Success() throws EntityNotFoundException {
    Order updateData = new Order();
    updateData.setId(1L);
    updateData.setStatus(null);
    updateData.setTotalPrice(50.0);

    when(orderDao.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testOrder));
    when(orderDao.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Order result = orderService.updateOrder(updateData);

    assertEquals("NEW", result.getStatus());
    assertEquals(50.0, result.getTotalPrice());
  }

  @Test
  void deleteOrder_Success() throws EntityNotFoundException {
    when(orderDao.findById(1L)).thenReturn(Optional.of(testOrder));
    orderService.deleteOrder(1L);

    verify(orderDao, times(1)).softDeleteById(1L);
  }

  @Test
  void deleteOrder_ThrowsNotFound_WhenOrderDoesNotExist() {
    when(orderDao.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> orderService.deleteOrder(1L));
    verify(orderDao, never()).softDeleteById(anyLong());
  }
}
