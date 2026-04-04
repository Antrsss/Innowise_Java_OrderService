package com.innowise.orderservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto {

  private Long id;

  @NotNull(message = "User ID is required")
  private Long userId;

  @NotNull(message = "Status is required")
  private String status;

  @NotNull(message = "Total price is required")
  private Double totalPrice;

  @NotEmpty(message = "Order must have at least one item")
  private List<OrderItemDto> items;
}
