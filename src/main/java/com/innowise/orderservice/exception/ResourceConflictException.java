package com.innowise.orderservice.exception;

import org.springframework.http.HttpStatus;

public class ResourceConflictException extends BaseServiceException {
  public ResourceConflictException(String message) {
    super(message, HttpStatus.CONFLICT);
  }
}
