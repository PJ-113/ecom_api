/*
package com.ecom2.controller;

import com.ecom2.entity.Product;
import com.ecom2.entity.User;
import com.ecom2.service.CartService;
import com.ecom2.service.ProductService;
import com.ecom2.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

  private final ProductService productService;
  private final CartService cartService;
  private final UserService userService;

  public ApiController(ProductService productService, CartService cartService, UserService userService) {
    this.productService = productService;
    this.cartService = cartService;
    this.userService = userService;
  }

  @GetMapping("/products")
  public ResponseEntity<?> products() {
    return ResponseEntity.ok(productService.all());
  }

  @PostMapping("/products")
  public ResponseEntity<?> create(@RequestBody Product p) {
    return ResponseEntity.ok(productService.save(p));
  }

  @GetMapping("/products/{id}")
  public ResponseEntity<?> get(@PathVariable Long id) {
    return ResponseEntity.ok(productService.get(id));
  }

  @DeleteMapping("/products/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    productService.softDelete(id);
    return ResponseEntity.ok(Map.of("status","deleted"));
  }

  @PostMapping("/cart/add/{productId}")
  public ResponseEntity<?> addToCart(@PathVariable Long productId,
                                     @RequestParam(defaultValue="1") int qty,
                                     Authentication auth) {
    User u = userService.getByEmail(auth.getName());
    cartService.add(u, productId, qty);
    return ResponseEntity.ok(Map.of("status","ok"));
  }

  @GetMapping("/cart")
  public ResponseEntity<?> cart(Authentication auth) {
    User u = userService.getByEmail(auth.getName());
    return ResponseEntity.ok(cartService.items(u));
  }
}
*/

