package com.innowise.orderservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.orderservice.dao.ItemDao;
import com.innowise.orderservice.dao.OrderDao;
import com.innowise.orderservice.dto.ItemDto;
import com.innowise.orderservice.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ItemIntegrationTest extends BaseIntegrationTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ItemDao itemDao;

  @Autowired
  private OrderDao orderDao;

  private ItemDto validItemDto;
  private Item existingItem;

  @BeforeEach
  void setUp() {
    orderDao.deleteAll();
    itemDao.deleteAll();

    validItemDto = ItemDto.builder()
        .name("Test Item")
        .price(99.99)
        .build();

    existingItem = Item.builder()
        .name("Existing Item")
        .price(49.99)
        .build();
    existingItem = itemDao.save(existingItem);

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
  }

  @Test
  void createItem_ShouldReturnCreatedItem() throws Exception {
    mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validItemDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.name").value("Test Item"))
        .andExpect(jsonPath("$.price").value(99.99));
  }

  @Test
  void createItem_WithInvalidData_ShouldReturnBadRequest() throws Exception {
    ItemDto invalidDto = ItemDto.builder()
        .name("")
        .price(-10.00)
        .build();

    mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getItem_WhenExists_ShouldReturnItem() throws Exception {
    mockMvc.perform(get("/api/items/{id}", existingItem.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(existingItem.getId()))
        .andExpect(jsonPath("$.name").value("Existing Item"))
        .andExpect(jsonPath("$.price").value(49.99));
  }

  @Test
  void getItem_WhenNotExists_ShouldReturnNotFound() throws Exception {
    mockMvc.perform(get("/api/items/{id}", 99999L))
        .andExpect(status().isNotFound());
  }

  @Test
  void getAllItems_ShouldReturnAllItems() throws Exception {
    itemDao.save(Item.builder()
        .name("Second Item")
        .price(29.99)
        .build());

    mockMvc.perform(get("/api/items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[*].name", containsInAnyOrder("Existing Item", "Second Item")));
  }

  @Test
  void updateItem_WhenExists_ShouldReturnUpdatedItem() throws Exception {
    ItemDto updateDto = ItemDto.builder()
        .name("Updated Item")
        .price(199.99)
        .build();

    mockMvc.perform(put("/api/items/{id}", existingItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(existingItem.getId()))
        .andExpect(jsonPath("$.name").value("Updated Item"))
        .andExpect(jsonPath("$.price").value(199.99));
  }

  @Test
  void updateItem_WhenNotExists_ShouldReturnNotFound() throws Exception {
    mockMvc.perform(put("/api/items/{id}", 99999L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validItemDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteItem_WhenExists_ShouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/api/items/{id}", existingItem.getId()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/items/{id}", existingItem.getId()))
        .andExpect(status().isNotFound());
  }
}