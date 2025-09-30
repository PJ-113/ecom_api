package com.ecom2.controller;



import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController {
	@GetMapping("/") public String home1(){ return "index"; }
	  @GetMapping("/products") public String products(){ return "products"; }
	  @GetMapping("/cart-page\"") public String cart(){ return "cart"; }
	  @GetMapping("/login") public String login(){ return "login"; }
	  @GetMapping("/register") public String register(){ return "register"; }
	  @GetMapping("/admin/products") public String adminProducts(){ return "admin-products"; }
  // เสิร์ฟไฟล์จาก /static/login.html
	// @GetMapping("/login")
	//public String login() {
	 // return "forward:/login.html";
	 // }

  // หน้า root ให้ไป swagger (กันลูปถ้าหน้าแรกต้อง auth)
  @GetMapping("/swagger")
  public String home() {
    return "redirect:/swagger-ui/index.html";
    
    
  }
}

