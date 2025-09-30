package com.ecom2.controller;

import com.ecom2.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Validated
public class AuthController {

  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }


  @PostMapping("/register")
  public String register(@ModelAttribute("form") @Validated RegisterForm f, Model m) {
    try {
      userService.register(f.getName(), f.getEmail(), f.getPassword(), false);
      m.addAttribute("msg", "Register success. Please login.");
      return "login";
    } catch (Exception e) {
      m.addAttribute("err", e.getMessage());
      return "register";
    }
  }

  public static class RegisterForm {
    @NotBlank private String name;
    @Email @NotBlank private String email;
    @NotBlank private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
  }
}
