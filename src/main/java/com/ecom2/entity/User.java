package com.ecom2.entity;

import jakarta.persistence.*;


import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

  public enum Role { ROLE_USER, ROLE_ADMIN }

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role = Role.ROLE_USER;

  private boolean enabled = true;

  // ความสัมพันธ์ One-to-One (back-reference)
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
  private UserProfile profile;

  public User() {}

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }

  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }

  public UserProfile getProfile() { return profile; }
  public void setProfile(UserProfile profile) {
    this.profile = profile;
    if (profile != null) profile.setUser(this); // sync ฝั่ง Profile
  }
}
