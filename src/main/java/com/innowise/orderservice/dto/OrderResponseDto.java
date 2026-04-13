package com.innowise.orderservice.dto;

public record OrderResponseDto(
    OrderDto order,
    UserDto user
) {}
