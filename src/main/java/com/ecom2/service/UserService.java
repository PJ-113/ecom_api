package com.ecom2.service;


import com.ecom2.entity.User;
import com.ecom2.entity.User.Role;
import com.ecom2.entity.UserProfile;
import com.ecom2.repo.UserProfileRepository;
import com.ecom2.repo.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserProfileRepository profileRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository,
                     UserProfileRepository profileRepository,
                     PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.profileRepository = profileRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /** สมัครสมาชิก: สร้าง User (email/password/role) + Profile (name) */
  @Transactional
  public User register(String name, String email, String rawPassword, boolean admin) {
    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("Email already used");
    }

    User u = new User();
    u.setEmail(email);
    u.setPassword(passwordEncoder.encode(rawPassword));
    u.setRole(admin ? Role.ROLE_ADMIN : Role.ROLE_USER);
    u.setEnabled(true);

    // บันทึก User ก่อน เพื่อให้มี id
    User saved = userRepository.save(u);

    // สร้างโปรไฟล์ผูกกับผู้ใช้
    UserProfile p = new UserProfile();
    p.setUser(saved);
    p.setName(name != null ? name : ""); // หรือปล่อย null ก็ได้ตามต้องการ
    profileRepository.save(p);

    // ผูก back-reference (ถ้า entity User มี field profile)
    saved.setProfile(p);
    return saved;
  }

  @Transactional(readOnly = true)
  public User getByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
  }

  /**
   * อัปเดตโปรไฟล์:
   * - email อยู่ในตาราง users
   * - name/phone อยู่ในตาราง user_profiles
   */
  @Transactional
  public User updateProfile(Long userId, String name, String email, String phone) {
    User u = userRepository.findById(userId).orElseThrow();

    // อัปเดต email (พร้อมตรวจซ้ำกับคนอื่น)
    if (email != null && !email.equalsIgnoreCase(u.getEmail())) {
      if (userRepository.existsByEmailAndIdNot(email, userId)) {
        throw new IllegalArgumentException("This email is already used by another account.");
      }
      u.setEmail(email);
    }

    // หา/สร้างโปรไฟล์ของ user แล้วอัปเดต name/phone
    UserProfile p = profileRepository.findByUserId(userId)
        .orElseGet(() -> {
          UserProfile np = new UserProfile();
          np.setUser(u);
          return np;
        });

    if (name != null)  p.setName(name);
    if (phone != null) p.setPhone(phone);

    profileRepository.save(p);
    // (ถ้ามี field profile ใน User)
    u.setProfile(p);

    return userRepository.save(u);
  }

  @Transactional
  public void changePassword(Long userId, String currentRaw, String newRaw) {
    User u = userRepository.findById(userId).orElseThrow();
    if (!passwordEncoder.matches(currentRaw, u.getPassword())) {
      throw new IllegalArgumentException("Current password is incorrect.");
    }
    u.setPassword(passwordEncoder.encode(newRaw));
    userRepository.save(u);
  }

  @Transactional(readOnly = true)
  public List<User> all() {
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public User get(Long id) {
    return userRepository.findById(id).orElseThrow();
  }

  /** บันทึก User; ถ้ามี password ใหม่ ค่อยเข้ารหัส */
  @Transactional
  public User save(User u) {
    if (u.getPassword() != null && !u.getPassword().isBlank()) {
      // ระวังอย่า re-encode รหัสผ่านที่เข้ารหัสแล้ว
      // (ถ้าจำเป็น ให้เช็คด้วยเงื่อนไขของโปรเจกต์คุณเอง)
      u.setPassword(passwordEncoder.encode(u.getPassword()));
    }
    return userRepository.save(u);
  }

  @Transactional
  public void delete(Long id) {
    // ถ้า FK ตั้ง ON DELETE CASCADE หรือมี orphanRemoval = true ที่ความสัมพันธ์ 1-1
    // การลบ User จะลบ Profile ให้เอง
    userRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Transactional(readOnly = true)
  public User findByEmail2(String email) {
    return userRepository.findByEmail(email).orElse(null);
  }
}

