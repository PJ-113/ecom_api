/*
package com.ecom2.controller;

import com.ecom2.entity.User;
import com.ecom2.service.OrderService;
import com.ecom2.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;
  private final UserService userService;

  public OrderController(OrderService orderService, UserService userService) {
    this.orderService = orderService;
    this.userService = userService;
  }

  private User currentUser(Authentication auth) {
    return userService.getByEmail(auth.getName());
  }

 // @PostMapping("/checkout")
 // public String checkout(Authentication auth) {
   // var order = orderService.checkout(currentUser(auth));
    //return "redirect:/orders/" + order.getId();
  //}

  @GetMapping("/{id}")
  public String detail(@PathVariable Long id, Authentication auth, Model m) {
    m.addAttribute("order", orderService.ofUser(currentUser(auth))
        .stream().filter(o -> o.getId().equals(id)).findFirst().orElseThrow());
    return "order_detail";
  }

  @GetMapping
  public String myOrders(Authentication auth, Model m) {
    m.addAttribute("orders", orderService.ofUser(currentUser(auth)));
    return "orders";
  }
}*/