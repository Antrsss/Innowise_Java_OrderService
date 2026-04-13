package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.ItemDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

  private final ItemService itemService;
  private final ItemMapper itemMapper;

  @PostMapping
  ResponseEntity<Item> createItem(@RequestBody @Valid ItemDto itemDto) {
    Item item = itemMapper.toEntity(itemDto);
    Item createdItem = itemService.createItem(item);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
  }

  @GetMapping("/{id}")
  ResponseEntity<ItemDto> getItem(@PathVariable Long id) {
    Item item = itemService.findItemById(id);
    ItemDto itemDto = itemMapper.toDto(item);

    return ResponseEntity.ok(itemDto);
  }

  @GetMapping
  ResponseEntity<List<ItemDto>> getAllItems() {
    List<Item> items = itemService.findAllItems();

    List<ItemDto> response = items.stream()
        .map(itemMapper::toDto)
        .toList();

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  ResponseEntity<ItemDto> updateItem(
      @PathVariable Long id,
      @RequestBody @Valid ItemDto itemDto) {

    Item item = itemMapper.toEntity(itemDto);
    item.setId(id);

    item = itemService.updateItem(item);
    ItemDto updatedItemDto = itemMapper.toDto(item);

    return ResponseEntity.ok(updatedItemDto);
  }

  @DeleteMapping("/{id}")
  ResponseEntity<ItemDto> deleteItem(@PathVariable Long id) {
    itemService.deleteItem(id);
    return ResponseEntity.noContent().build();
  }
}

