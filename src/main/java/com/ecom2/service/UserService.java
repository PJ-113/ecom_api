package com.ecom2.service;

import com.ecom2.entity.Role;
import com.ecom2.entity.User;
import com.ecom2.repo.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User register(String name, String email, String rawPassword, boolean admin) {
    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("Email already used");
    }
    User u = new User();
    u.setName(name);
    u.setEmail(email);
    u.setPassword(passwordEncoder.encode(rawPassword));
    u.setRole(admin ? Role.ROLE_ADMIN : Role.ROLE_USER);
    u.setEnabled(true);
    return userRepository.save(u);
  }

  public User getByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
  }
  
  public User updateProfile(Long userId, String name, String email, String phone) {
	    User u = userRepository.findById(userId).orElseThrow();
	    if (email != null && !email.equalsIgnoreCase(u.getEmail())) {
	      if (userRepository.existsByEmailAndIdNot(email, userId)) {
	        throw new IllegalArgumentException("This email is already used by another account.");
	      }
	      u.setEmail(email);
	    }
	    if (name != null) u.setName(name);
	    if (phone != null) u.setPhone(phone);
	    return userRepository.save(u);
	  }

	  public void changePassword(Long userId, String currentRaw, String newRaw) {
	    User u = userRepository.findById(userId).orElseThrow();
	    if (!passwordEncoder.matches(currentRaw, u.getPassword())) {
	      throw new IllegalArgumentException("Current password is incorrect.");
	    }
	    u.setPassword(passwordEncoder.encode(newRaw));
	    userRepository.save(u);
	  }
	  
	  public List<User> all() {
	        return userRepository.findAll();
	    }

	    public User get(Long id) {
	        return userRepository.findById(id).orElseThrow();
	    }

	    public User save(User u) {
	        // ถ้าต้องการเข้ารหัสรหัสผ่าน
	        if (u.getPassword() != null && !u.getPassword().isBlank()) {
	            u.setPassword(passwordEncoder.encode(u.getPassword()));
	        }
	        return userRepository.save(u);
	    }

	    public void delete(Long id) {
	        userRepository.deleteById(id);
	    }
	    public Optional<User> findByEmail(String email) {
	    	  return userRepository.findByEmail(email);
	    	}
	    public User findByEmail2(String email) {
	        return userRepository.findByEmail(email).orElse(null);
	      }
	}


