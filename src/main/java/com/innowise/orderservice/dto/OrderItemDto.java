package com.innowise.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemDto {

  private Long id;

  @NotNull(message = "Order ID is required")
  private Long orderId;

  @NotNull(message = "Item ID is required")
  private Long itemId;

  @Min(value = 1, message = "Quantity must be at least 1")
  private Integer quantity;
}
