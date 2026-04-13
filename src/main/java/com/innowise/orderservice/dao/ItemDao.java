package com.innowise.orderservice.dao;

import com.innowise.orderservice.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemDao extends JpaRepository<Item, Long> {
  Optional<Item> findByName(String name);
}
