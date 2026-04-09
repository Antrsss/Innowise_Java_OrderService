package com.innowise.orderservice.service;

import com.innowise.orderservice.dao.ItemDao;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.exception.EntityNotFoundException;
import com.innowise.orderservice.exception.ResourceConflictException;
import com.innowise.orderservice.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

  @Mock
  private ItemDao itemDao;

  @InjectMocks
  private ItemServiceImpl itemService;

  private Item item;
  private Item existingItem;

  @BeforeEach
  void setUp() {
    item = new Item();
    item.setName("Laptop");
    item.setPrice(999.99);

    existingItem = new Item();
    existingItem.setId(1L);
    existingItem.setName("Phone");
    existingItem.setPrice(499.99);
  }

  @Test
  void createItem_ShouldSaveItem_WhenItemDoesNotExist() {
    when(itemDao.findByName(item.getName())).thenReturn(Optional.empty());
    when(itemDao.save(any(Item.class))).thenReturn(item);
    Item savedItem = itemService.createItem(item);

    assertThat(savedItem).isNotNull();
    assertThat(savedItem.getName()).isEqualTo("Laptop");
    verify(itemDao).save(item);
    verify(itemDao, never()).findById(any());
  }

  @Test
  void createItem_ShouldThrowException_WhenItemWithIdExists() {
    item.setId(1L);
    when(itemDao.findById(1L)).thenReturn(Optional.of(existingItem));

    assertThatThrownBy(() -> itemService.createItem(item))
        .isInstanceOf(ResourceConflictException.class)
        .hasMessage("Item already exists");

    verify(itemDao, never()).save(any());
  }

  @Test
  void createItem_ShouldThrowException_WhenItemWithSameNameExists() {
    when(itemDao.findByName(item.getName())).thenReturn(Optional.of(existingItem));

    assertThatThrownBy(() -> itemService.createItem(item))
        .isInstanceOf(ResourceConflictException.class)
        .hasMessage("Item already exists");

    verify(itemDao, never()).save(any());
  }

  @Test
  void findItemById_ShouldReturnItem_WhenItemExists() {
    when(itemDao.findById(1L)).thenReturn(Optional.of(existingItem));
    Item foundItem = itemService.findItemById(1L);

    assertThat(foundItem).isNotNull();
    assertThat(foundItem.getId()).isEqualTo(1L);
    assertThat(foundItem.getName()).isEqualTo("Phone");
    assertThat(foundItem.getPrice()).isEqualTo(499.99);
  }

  @Test
  void findItemById_ShouldThrowException_WhenItemNotFound() {
    when(itemDao.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> itemService.findItemById(999L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Item not found");
  }

  @Test
  void findAllItems_ShouldReturnAllItems() {
    List<Item> items = List.of(existingItem, item);
    when(itemDao.findAll()).thenReturn(items);

    List<Item> result = itemService.findAllItems();

    assertThat(result)
        .hasSize(2)
        .containsExactly(existingItem, item);
    verify(itemDao).findAll();
  }

  @Test
  void findAllItems_ShouldReturnEmptyList_WhenNoItemsExist() {
    when(itemDao.findAll()).thenReturn(List.of());
    List<Item> result = itemService.findAllItems();

    assertThat(result).isEmpty();
    verify(itemDao).findAll();
  }

  @Test
  void updateItem_ShouldUpdateName_WhenNameIsProvided() {
    Item updateRequest = new Item();
    updateRequest.setId(1L);
    updateRequest.setName("Updated Phone");

    when(itemDao.findById(1L)).thenReturn(Optional.of(existingItem));
    when(itemDao.save(any(Item.class))).thenReturn(existingItem);
    Item updatedItem = itemService.updateItem(updateRequest);

    assertThat(updatedItem.getName()).isEqualTo("Updated Phone");
    assertThat(updatedItem.getPrice()).isEqualTo(499.99);
    verify(itemDao).save(existingItem);
  }

  @Test
  void updateItem_ShouldUpdatePrice_WhenPriceIsProvided() {
    Item updateRequest = new Item();
    updateRequest.setId(1L);
    updateRequest.setPrice(599.99);

    when(itemDao.findById(1L)).thenReturn(Optional.of(existingItem));
    when(itemDao.save(any(Item.class))).thenReturn(existingItem);
    Item updatedItem = itemService.updateItem(updateRequest);

    assertThat(updatedItem.getPrice()).isEqualTo(599.99);
    assertThat(updatedItem.getName()).isEqualTo("Phone");
    verify(itemDao).save(existingItem);
  }

  @Test
  void updateItem_ShouldUpdateBothNameAndPrice_WhenBothAreProvided() {
    Item updateRequest = new Item();
    updateRequest.setId(1L);
    updateRequest.setName("Smartphone");
    updateRequest.setPrice(699.99);

    when(itemDao.findById(1L)).thenReturn(Optional.of(existingItem));
    when(itemDao.save(any(Item.class))).thenReturn(existingItem);
    Item updatedItem = itemService.updateItem(updateRequest);

    assertThat(updatedItem.getName()).isEqualTo("Smartphone");
    assertThat(updatedItem.getPrice()).isEqualTo(699.99);
    verify(itemDao).save(existingItem);
  }

  @Test
  void updateItem_ShouldThrowException_WhenItemNotFound() {
    Item updateRequest = new Item();
    updateRequest.setId(999L);
    updateRequest.setName("Non-existent");

    when(itemDao.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> itemService.updateItem(updateRequest))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Item not found");

    verify(itemDao, never()).save(any());
  }

  @Test
  void deleteItem_ShouldDeleteItem_WhenItemExists() {
    when(itemDao.findById(1L)).thenReturn(Optional.of(existingItem));
    doNothing().when(itemDao).delete(existingItem);

    itemService.deleteItem(1L);

    verify(itemDao).findById(1L);
    verify(itemDao).delete(existingItem);
  }

  @Test
  void deleteItem_ShouldThrowException_WhenItemNotFound() {
    when(itemDao.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> itemService.deleteItem(999L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Item not found");

    verify(itemDao, never()).delete(any());
  }
}
