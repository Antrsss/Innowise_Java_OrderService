package com.innowise.orderservice.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Table(name = "items")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item extends AbstractEntity {
  private String name;
  private Double price;
}

