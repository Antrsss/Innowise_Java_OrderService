package com.innowise.orderservice.specification;

import com.innowise.orderservice.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {

  private OrderSpecification() {}

  public static Specification<Order> isNotDeleted() {
    return (root, query, cb) -> cb.isFalse(root.get("deleted"));
  }

  public static Specification<Order> hasStatus(String status) {
    return (root, query, cb) -> {
      if (status == null || status.isEmpty()) return null;
      return cb.equal(root.get("status"), status);
    };
  }

  public static Specification<Order> createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
    return (root, query, cb) -> {
      if (startDate == null && endDate == null) return null;

      Predicate predicate = cb.conjunction();
      if (startDate != null) {
        predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
      }
      if (endDate != null) {
        predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
      }
      return predicate;
    };
  }
}

