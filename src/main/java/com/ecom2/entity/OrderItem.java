package com.ecom2.entity;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_items")
public class OrderItem {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @ManyToOne(optional = false)
  private Product product;

  private int quantity;

  private BigDecimal price;

  // ✅ เพิ่ม mapping ย้อนกลับไปยัง RedeemCode
  @OneToMany(mappedBy = "orderItem", fetch = FetchType.LAZY)
  private List<RedeemCode> redeemCodes = new ArrayList<>();
  
  

  public OrderItem() {}

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Order getOrder() { return order; }
  public void setOrder(Order order) { this.order = order; }

  public Product getProduct() { return product; }
  public void setProduct(Product product) { this.product = product; }

  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }

  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }

  public List<RedeemCode> getRedeemCodes() { return redeemCodes; }
  public void setRedeemCodes(List<RedeemCode> redeemCodes) { this.redeemCodes = redeemCodes; }
}
