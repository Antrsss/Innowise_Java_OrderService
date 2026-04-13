package com.innowise.orderservice.dto;

import lombok.Data;

@Data
public class UserDto {
  private Long id;
  private String email;
  private String name;
  private String surname;
}
