package com.ecom2.api;


import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.ecom2.api.dto.UserDto;
import com.ecom2.api.dto.UserProfileDto;
import com.ecom2.api.mapper.ApiMapper;
import com.ecom2.entity.User;
import com.ecom2.service.UserProfileService;
import com.ecom2.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserRestController {

	private final UserService userService;
    private final UserProfileService userProfileService; 

    public UserRestController(UserService userService,
                              UserProfileService userProfileService) {  
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public List<UserDto> all(){
        return userService.all().stream().map(ApiMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id){
        return ApiMapper.toDto(userService.get(id));
    }

    /*@PostMapping
    public UserDto create(@RequestBody @Valid UserDto in){
        boolean admin = "ROLE_ADMIN".equalsIgnoreCase(in.role());

        User saved = userService.register(in.name(), in.email(), "changeme", admin);

        if (in.phone() != null) {
            userService.updateProfile(saved.getId(), null, null, in.phone());
        }
        return ApiMapper.toDto(userService.get(saved.getId()));
    }*/

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody @Valid UserDto in){
        User updated = userService.updateProfile(id, in.name(), in.email(), in.phone());

        return ApiMapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        userService.delete(id);
    }
    
    @GetMapping("/{id}/profile")
    public UserProfileDto getProfile(@PathVariable Long id){
      var p = userProfileService.getByUserId(id);
      return new UserProfileDto(p.getName(), p.getPhone());
    }

    @PutMapping("/{id}/profile")
    public UserProfileDto upsertProfile(@PathVariable Long id, @RequestBody @Valid UserProfileDto in){
      var p = userProfileService.upsert(id, in);
      return new UserProfileDto(p.getName(), p.getPhone());
    }
}
