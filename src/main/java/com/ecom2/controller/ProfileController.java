package com.ecom2.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecom2.entity.User;
import com.ecom2.service.UserService;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Controller
@RequestMapping("/profile")
@Validated
public class ProfileController {

  private final UserService userService;

  public ProfileController(UserService userService) {
    this.userService = userService;
  }

  private User currentUser(Authentication auth) {
    return userService.getByEmail(auth.getName());
  }

 
  @GetMapping
  public String view(Model model, Authentication auth) {
      User u = currentUser(auth);
      ProfileForm f = new ProfileForm();
      f.setName(u.getName());
      f.setEmail(u.getEmail());
      f.setPhone(u.getPhone());
      model.addAttribute("form", f);
      return "profile";
  }

  @PostMapping
  public String update(@ModelAttribute("form") @Validated ProfileForm f,
                       Authentication auth, RedirectAttributes ra) {
    User u = currentUser(auth);
    try {
      userService.updateProfile(u.getId(), f.getName(), f.getEmail(), f.getPhone());
      ra.addFlashAttribute("msg", "Profile updated.");
      return "redirect:/profile";
    } catch (IllegalArgumentException ex) {
      ra.addFlashAttribute("err", ex.getMessage());
      return "redirect:/profile";
    }
  }


  @GetMapping("/password")
  public String passwordForm() {
    return "profile_password";
  }

  @PostMapping("/password")
  public String changePassword(@RequestParam String currentPassword,
                               @RequestParam @Size(min = 6, message="New password must be at least 6 chars") String newPassword,
                               @RequestParam String confirmPassword,
                               Authentication auth,
                               RedirectAttributes ra) {
    if (!newPassword.equals(confirmPassword)) {
      ra.addFlashAttribute("err", "New password and confirm do not match.");
      return "redirect:/profile/password";
    }
    try {
      userService.changePassword(currentUser(auth).getId(), currentPassword, newPassword);
      ra.addFlashAttribute("msg", "Password changed successfully.");
      return "redirect:/profile/password";
    } catch (IllegalArgumentException ex) {
      ra.addFlashAttribute("err", ex.getMessage());
      return "redirect:/profile/password";
    }
  }

  // ====== DTO ======
  public static class ProfileForm {
    @NotBlank private String name;
    @Email @NotBlank private String email;
    private String phone;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
  }
}
