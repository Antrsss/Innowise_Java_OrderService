package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.dao.ItemDao;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.exception.EntityNotFoundException;
import com.innowise.orderservice.exception.ResourceConflictException;
import com.innowise.orderservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemDao itemDao;

  @Override
  public Item createItem(Item item) throws ResourceConflictException, EntityNotFoundException {
    if (item.getId() != null &&
        itemDao.findById(item.getId()).isPresent() ||
        itemDao.findByName(item.getName()).isPresent()) {
      throw new ResourceConflictException("Item already exists");
    }

    return itemDao.save(item);
  }

  @Override
  public Item findItemById(Long id) throws EntityNotFoundException {
    return itemDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Item not found"));
  }

  @Override
  public List<Item> findAllItems() {
    return itemDao.findAll();
  }

  @Override
  public Item updateItem(Item item) throws EntityNotFoundException {
    Item existingItem = findItemById(item.getId());

    if (item.getName() != null) {
      existingItem.setName(item.getName());
    }

    if (item.getPrice() != null && item.getPrice() >= 0) {
      existingItem.setPrice(item.getPrice());
    }

    return itemDao.save(existingItem);
  }

  @Override
  public void deleteItem(Long id) throws EntityNotFoundException {
    itemDao.delete(findItemById(id));
  }
}

