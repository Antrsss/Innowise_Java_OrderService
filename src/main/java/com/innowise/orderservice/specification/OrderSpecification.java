package com.innowise.orderservice.specification;

import com.innowise.orderservice.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Collection;

public class OrderSpecification {

  private OrderSpecification() {}

  public static Specification<Order> isNotDeleted() {
    return (root, query, cb) -> cb.isFalse(root.get("deleted"));
  }

  public static Specification<Order> hasUserId(Long userId) {
    return (root, query, cb) -> {
      if (userId == null)
        return cb.conjunction();

      return cb.equal(root.get("userId"), userId);
    };
  }

  public static Specification<Order> hasStatuses(Collection<String> statuses) {
    return (root, query, cb) -> {
      if (statuses == null || statuses.isEmpty())
        return cb.conjunction();

      return root.get("status").in(statuses);
    };
  }

  public static Specification<Order> createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
    return (root, query, cb) -> {
      if (startDate == null && endDate == null)
        return cb.conjunction();

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

