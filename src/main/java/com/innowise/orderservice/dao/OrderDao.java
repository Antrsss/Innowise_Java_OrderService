package com.innowise.orderservice.dao;

import com.innowise.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderDao extends JpaRepository<Order, Long>,
    JpaSpecificationExecutor<Order> {

  Optional<Order> findByIdAndDeletedFalse(Long orderId);

  List<Order> findAllByUserIdAndDeletedFalse(Long userId);

  @Modifying
  @Query("UPDATE Order o SET o.deleted = true WHERE o.id = :id")
  void softDeleteById(@Param("id") Long id);
}
