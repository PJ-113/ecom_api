package com.ecom2.repo;

import com.ecom2.entity.CartItem;
import com.ecom2.entity.User;
import com.ecom2.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  List<CartItem> findByUser(User user);
  Optional<CartItem> findByUserAndProduct(User user, Product product);
  void deleteByUser(User user);
}

