package com.ecom2.service;

import com.ecom2.entity.CartItem;
import com.ecom2.entity.Product;
import com.ecom2.entity.User;
import com.ecom2.repo.CartItemRepository;
import com.ecom2.repo.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

  private final CartItemRepository cartRepo;
  private final ProductRepository productRepo;

  public CartService(CartItemRepository cartRepo, ProductRepository productRepo) {
    this.cartRepo = cartRepo;
    this.productRepo = productRepo;
  }

  public List<CartItem> items(User user) {
    return cartRepo.findByUser(user);
  }

  public void add(User user, Long productId, int qty) {
    Product p = productRepo.findById(productId).orElseThrow();
    CartItem item = cartRepo.findByUserAndProduct(user, p).orElse(null);
    if (item == null) {
      item = new CartItem();
      item.setUser(user);
      item.setProduct(p);
      item.setQuantity(Math.max(1, qty));
    } else {
      item.setQuantity(Math.max(1, item.getQuantity() + qty));
    }
    cartRepo.save(item);
  }

  public void update(User user, Long itemId, int qty) {
    CartItem item = cartRepo.findById(itemId).orElseThrow();
    if (!item.getUser().getId().equals(user.getId())) throw new RuntimeException("Forbidden");
    item.setQuantity(Math.max(1, qty));
    cartRepo.save(item);
  }

  public void remove(User user, Long itemId) {
    CartItem item = cartRepo.findById(itemId).orElseThrow();
    if (!item.getUser().getId().equals(user.getId())) throw new RuntimeException("Forbidden");
    cartRepo.delete(item);
  }
  
  @Transactional
  public void clear(User user) {
    cartRepo.deleteByUser(user);
  }
  
  public List<CartItem> getCart(User user) {
	  return items(user); // ใช้ของเดิม
	}

	public List<CartItem> addToCart(User user, Long productId, int qty) {
	  add(user, productId, qty);                 // ใช้ของเดิม
	  return cartRepo.findByUser(user);          // คืนรายการล่าสุดให้ controller
	}

	public List<CartItem> updateQuantity(User user, Long productId, int qty) {
	  Product p = productRepo.findById(productId).orElseThrow();
	  CartItem item = cartRepo.findByUserAndProduct(user, p).orElseThrow();
	  item.setQuantity(Math.max(1, qty));
	  cartRepo.save(item);
	  return cartRepo.findByUser(user);
	}

	public List<CartItem> removeFromCart(User user, Long productId) {
	  Product p = productRepo.findById(productId).orElseThrow();
	  cartRepo.findByUserAndProduct(user, p).ifPresent(cartRepo::delete);
	  return cartRepo.findByUser(user);
	}

	public void clearCart(User user) {
	  clear(user); // เรียกตัวเดิมของคุณ
	}
}
