package com.innowise.orderservice.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter @Setter
public class Item extends AbstractEntity {
  private String name;
  private Double price;
}

