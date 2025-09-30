package com.ecom2.entity;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private User user;

  private LocalDateTime createdAt;

  @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval=true)
  private List<OrderItem> items = new ArrayList<>();

  private BigDecimal total;
  
  @Column(length = 20)
  private String status;

  public Order() {}

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public List<OrderItem> getItems() { return items; }
  public void setItems(List<OrderItem> items) { this.items = items; }

  public BigDecimal getTotal() { return total; }
  public void setTotal(BigDecimal total) { this.total = total; }
  
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
}

