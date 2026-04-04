package com.innowise.orderservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseServiceException extends RuntimeException {

  private final HttpStatus status;

  BaseServiceException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }
}

