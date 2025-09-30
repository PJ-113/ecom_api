package com.ecom2.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  public enum FulfillmentType { PHYSICAL, DIGITAL_CODE }

  @Column(nullable = false)
  private String name;

  @Column(length = 2000)
  private String description;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal price;
  
  @Column(nullable = false)
  private long stock = 0L;
  @Column(nullable = false)
  private boolean active = true;

  private String imagePath;
  


  public Product() {}

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }

  public String getImagePath() { return imagePath; }
  public void setImagePath(String imagePath) { this.imagePath = imagePath; }
  
  public boolean getActive() {return active;}
  public void setActive(boolean active) {this.active = active;}

 
  
	
}


