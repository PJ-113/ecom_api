package com.ecom2.repo;


import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom2.entity.UserProfile;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
