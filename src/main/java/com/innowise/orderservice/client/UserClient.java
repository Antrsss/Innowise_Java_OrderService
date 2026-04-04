package com.innowise.orderservice.client;

import com.innowise.orderservice.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "user-service", url = "${services.user-service.url}")
public interface UserClient {

  @GetMapping("/api/users")
  @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "findUserFallback")
  UserDto findUserByEmail(@RequestParam("email") String email);

  @GetMapping
  @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "findUserFallback")
  Optional<UserDto> findUserById(@RequestParam("id") Long id);

  default UserDto findUserFallback(String email) {
    UserDto fallbackUser = new UserDto();
    fallbackUser.setEmail(email);
    fallbackUser.setName("Service Unavailable");
    return fallbackUser;
  }
}
