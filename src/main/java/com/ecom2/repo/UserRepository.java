package com.ecom2.repo;

import com.ecom2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  boolean existsByEmail(String email);
  
  boolean existsByEmailAndIdNot(String email, Long id);
}

