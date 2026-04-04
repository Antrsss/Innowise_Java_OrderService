package com.innowise.orderservice.service;

import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.exception.EntityNotFoundException;
import com.innowise.orderservice.exception.ResourceConflictException;

import java.util.List;

public interface ItemService {
  Item createItem(Item item) throws ResourceConflictException, EntityNotFoundException;
  Item findItemById(Long id) throws EntityNotFoundException;
  List<Item> findAllItems();
  Item updateItem(Item item) throws EntityNotFoundException;
  void deleteItem(Long id) throws EntityNotFoundException;
}
