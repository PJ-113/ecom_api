/*
package com.ecom2.controller;

import com.ecom2.entity.User;
import com.ecom2.service.CartService;
import com.ecom2.service.UserService;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

  private final CartService cartService;
  private final UserService userService;

  public CartController(CartService cartService, UserService userService) {
    this.cartService = cartService;
    this.userService = userService;
  }

  /** ดึงผู้ใช้ปัจจุบัน ถ้าไม่ล็อกอินจะคืนค่า null 
  private User currentUser(Principal principal) {
	  if (principal == null) return null;
	  return userService.getByEmail(principal.getName());
	}

  /** แสดงตะกร้า 
  @GetMapping
  public String view(Model m, Principal principal, RedirectAttributes ra) {
    var u = currentUser(principal);
    if (u == null) return "redirect:/login";

    var items = cartService.items(u);
    var total = items.stream()
        .map(it -> it.getProduct().getPrice()
            .multiply(java.math.BigDecimal.valueOf(it.getQuantity())))
        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

    m.addAttribute("items", items);
    m.addAttribute("total", total);
    return "cart";
  }

  @PostMapping("/add/{productId}")
  public String add(@PathVariable Long productId,
                    @RequestParam(defaultValue = "1") int qty,
                    Principal principal, RedirectAttributes ra) {
    var u = currentUser(principal);
    if (u == null) return "redirect:/login";
    cartService.add(u, productId, Math.max(1, qty));
    ra.addFlashAttribute("msg", "✅ เพิ่มสินค้าลงตะกร้าเรียบร้อย");
    return "redirect:/";
  }

  @PostMapping("/update/{itemId}")
  public String update(@PathVariable Long itemId,
                       @RequestParam int qty,
                       Principal principal) {
    var u = currentUser(principal);
    if (u == null) return "redirect:/login";
    cartService.update(u, itemId, Math.max(1, qty));
    return "redirect:/cart";
  }

  @PostMapping("/remove/{itemId}")
  public String remove(@PathVariable Long itemId, Principal principal) {
    var u = currentUser(principal);
    if (u == null) return "redirect:/login";
    cartService.remove(u, itemId);
    return "redirect:/cart";
  }

  @PostMapping("/clear")
  public String clear(Principal principal) {
    var u = currentUser(principal);
    if (u == null) return "redirect:/login";
    cartService.clear(u);
    return "redirect:/cart";
  }

}*/