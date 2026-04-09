package com.innowise.orderservice.dao;

import com.innowise.orderservice.entity.Order;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderDao extends JpaRepository<Order, Long>,
    JpaSpecificationExecutor<Order> {

  @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
  Optional<Order> findByIdAndDeletedFalse(Long orderId);

  @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
  List<Order> findAllByUserIdAndDeletedFalse(Long userId);

  @Modifying
  @Query("UPDATE Order o SET o.deleted = true WHERE o.id = :id")
  void softDeleteById(@Param("id") Long id);
}
