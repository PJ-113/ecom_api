package com.ecom2.config;


import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


import com.ecom2.entity.User;
import com.ecom2.entity.UserProfile;
import com.ecom2.repo.UserRepository;

@Configuration
public class DataSeeder {

  private final UserRepository userRepo;
  private final PasswordEncoder encoder;

  public DataSeeder(UserRepository userRepo, PasswordEncoder encoder) {
    this.userRepo = userRepo;
    this.encoder = encoder;
  }

  @Bean
  ApplicationRunner initAdmin() {
    return args -> {
      // --- Admin ---
      String adminEmail = "admin@example.com";
      if (!userRepo.existsByEmail(adminEmail)) {
        User admin = new User();
        admin.setEmail(adminEmail);
        admin.setPassword(encoder.encode("admin123"));
        admin.setRole(User.Role.ROLE_ADMIN);   // ✅ ใช้ enum จาก User
        admin.setEnabled(true);

        // โปรไฟล์ของ Admin
        UserProfile adminProfile = new UserProfile();
        adminProfile.setName("Admin");
        adminProfile.setPhone("080-000-0000");

        // ผูกโปรไฟล์กับ user (setter ใน User จะ sync profile.setUser(this) ให้อัตโนมัติ)
        admin.setProfile(adminProfile);

        // บันทึกครั้งเดียวพอ (cascade = ALL จะ persist โปรไฟล์ด้วย)
        userRepo.save(admin);

        System.out.println("===> Seeded admin: " + adminEmail + " / admin123");
      }
    };
  }
}



