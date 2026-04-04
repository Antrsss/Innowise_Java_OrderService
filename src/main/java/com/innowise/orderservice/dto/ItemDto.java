package com.innowise.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ItemDto {

  private Long id;

  @NotBlank(message = "Name is required")
  private String name;

  @NotNull(message = "Price is required")
  @Positive(message = "Price must be positive")
  private Double price;
}
