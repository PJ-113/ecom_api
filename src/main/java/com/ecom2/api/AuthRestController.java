package com.ecom2.api;

import com.ecom2.api.dto.UserDto;
import com.ecom2.entity.User;
import com.ecom2.security.JwtService;
import com.ecom2.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

  private final UserService userService;
  private final PasswordEncoder encoder;
  private final JwtService jwtService;   

  public AuthRestController(UserService userService, PasswordEncoder encoder, JwtService jwtService) {
    this.userService = userService;
    this.encoder = encoder;
    this.jwtService = jwtService;
  }

  // ===== DTOs =====
  public record RegisterReq(String name, String email, String password, String phone) {}
  public record LoginReq(String email, String password) {}
  public record LoginRes(String token, UserDto user) {}

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterReq in) {
    if (in == null || in.name()==null || in.email()==null || in.password()==null) {
      return ResponseEntity.badRequest().body(Map.of("message","name, email, password required"));
    }
    try {
      User u = userService.register(in.name(), in.email().trim().toLowerCase(), in.password(), false);

      if (in.phone()!=null && !in.phone().isBlank()) {
        u = userService.updateProfile(u.getId(), null, null, in.phone());
      }

      String role = String.valueOf(u.getRole());
      var dto = new UserDto(u.getId(), u.getName(), u.getEmail(), u.getPhone(), role);

      
      UserDetails userDetails = org.springframework.security.core.userdetails.User
              .withUsername(u.getEmail())
              .password(u.getPassword())
              .roles(role.replace("ROLE_",""))
              .build();
      String token = jwtService.generateToken(userDetails);

      return ResponseEntity.ok(new LoginRes(token, dto));
    } catch (IllegalStateException ex) {
      return ResponseEntity.status(409).body(Map.of("message", ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.internalServerError().body(Map.of("message", "Register failed"));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginReq in) {
    if (in == null || in.email()==null || in.password()==null) {
      return ResponseEntity.badRequest().body(Map.of("message","email and password required"));
    }

    User u = userService.findByEmail2(in.email());
    if (u == null || !encoder.matches(in.password(), u.getPassword())) {
      return ResponseEntity.status(401).body(Map.of("message","Invalid credentials"));
    }

    String role = String.valueOf(u.getRole());
    var dto = new UserDto(u.getId(), u.getName(), u.getEmail(), u.getPhone(), role);

    
    UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(u.getEmail())
            .password(u.getPassword())
            .roles(role.replace("ROLE_",""))
            .build();
    String token = jwtService.generateToken(userDetails);

    return ResponseEntity.ok(new LoginRes(token, dto));
  }
}