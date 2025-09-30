package com.ecom2.service;

import com.ecom2.api.dto.UserProfileDto;
import com.ecom2.entity.User;
import com.ecom2.entity.UserProfile;
import com.ecom2.repo.UserProfileRepository;
import com.ecom2.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;

    public UserProfileService(UserRepository userRepo, UserProfileRepository profileRepo) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
    }

    @Transactional(readOnly = true)
    public UserProfile getByUserId(Long userId) {
        return profileRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user " + userId));
    }

    @Transactional
    public UserProfile upsert(Long userId, UserProfileDto in) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        
        UserProfile profile = profileRepo.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile np = new UserProfile();
                    np.setUser(user);
                    return np;
                });

        if (in.name() != null) profile.setName(in.name());
        if (in.phone() != null) profile.setPhone(in.phone());

        return profileRepo.save(profile);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        profileRepo.findByUserId(userId).ifPresent(profileRepo::delete);
    }
}