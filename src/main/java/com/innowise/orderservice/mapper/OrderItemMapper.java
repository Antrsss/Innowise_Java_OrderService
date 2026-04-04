package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.OrderItemDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

  default OrderItem toEntity(OrderItemDto dto) {
    if (dto == null) return null;

    OrderItem entity = new OrderItem();
    entity.setId(dto.getId());
    entity.setQuantity(dto.getQuantity());

    if (dto.getItemId() != null) {
      Item item = new Item();
      item.setId(dto.getItemId());
      entity.setItem(item);
    }

    return entity;
  }
}
