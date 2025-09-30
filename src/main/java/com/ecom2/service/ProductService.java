package com.ecom2.service;

import com.ecom2.entity.Product;
import com.ecom2.repo.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

  private final ProductRepository repo;

  public ProductService(ProductRepository repo) { this.repo = repo; }
  //--for user
  public List<Product> all() { return repo.findByActiveTrue(); }
  
  //--for admin
  public List<Product> activeProducts()   { return repo.findByActiveTrue();  }
  public List<Product> inactiveProducts() { return repo.findByActiveFalse(); }

  
  public Product get(Long id) {
      return repo.findByIdAndActiveTrue(id)
              .orElseThrow(() -> new RuntimeException("Product not found"));
  }
  // for admin
  public Product getAny(Long id) {
	    return repo.findById(id)
	        .orElseThrow(() -> new RuntimeException("Product not found"));
	  }

  public Product save(Product p) { return repo.save(p); }
  
  public void softDelete(Long id) {
      Product p = repo.findById(id)
              .orElseThrow(() -> new RuntimeException("Product not found"));
      if (!p.getActive()) return; 
      p.setActive(false);
      repo.save(p);
  }
  
  public void setActive(Long id, boolean active) {
	    Product p = repo.findById(id).orElseThrow();
	    p.setActive(active);
	    repo.save(p);
	}
  
  public void toggleActive(Long id) {
	    Product p = getAny(id);
	    p.setActive(!p.getActive());
	    repo.save(p);
	  }
  
  public List<Product> allForAdmin() { return repo.findAll(); }
  
}

