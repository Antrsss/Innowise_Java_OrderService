package com.innowise.orderservice.integration;

import com.innowise.orderservice.dao.ItemDao;
import com.innowise.orderservice.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.OrderItemDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderIntegrationTest extends BaseIntegrationTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ItemDao itemDao;

  Long itemId;

  @BeforeEach
  void setup() {
    Item item = new Item();
    item.setName("Test Item");
    item.setPrice(75.0);
    itemDao.save(item);

    itemId = item.getId();

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
  }

  @Test
  void createOrder_shouldReturnCreatedOrderWithUserInfo() throws Exception {

    wiremock.stubFor(get(urlPathEqualTo("/"))
        .withQueryParam("id", equalTo("100"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"id\":100,\"name\":\"Test User\"}")));

    wiremock.stubFor(get(urlPathEqualTo("/api/users"))
        .withQueryParam("email", equalTo("test@innowise.com"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
                {
                  "id":100,
                  "name":"Test User",
                  "email":"test@innowise.com"
                }
                """)));

    OrderItemDto item = new OrderItemDto();
    item.setItemId(itemId);
    item.setQuantity(2);

    OrderDto orderDto = new OrderDto();
    orderDto.setUserId(100L);
    orderDto.setStatus("NEW");
    orderDto.setTotalPrice(150.0);
    orderDto.setItems(List.of(item));

    mockMvc.perform(post("/api/orders")
            .header("X-User-Email", "test@innowise.com")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
             {
               "userId": 100,
               "status": "NEW",
               "totalPrice": 150.0,
               "items": [
                 {
                   "itemId": %d,
                   "quantity": 2
                 }
               ]
             }
            """.formatted(itemId)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.order.id").value(1))
        .andExpect(jsonPath("$.user.name").value("Test User"));
  }
}