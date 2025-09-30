package com.ecom2.api;


import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.ecom2.api.dto.UserDto;
import com.ecom2.api.mapper.ApiMapper;
import com.ecom2.entity.User;
import com.ecom2.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> all(){
        return userService.all().stream().map(ApiMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id){
        return ApiMapper.toDto(userService.get(id));
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto in){
        User u = new User();
        u.setName(in.name());
        u.setEmail(in.email());
        u.setPhone(in.phone());
        // set default password for REST creation (or expect another endpoint)
        u.setPassword("changeme");
        return ApiMapper.toDto(userService.save(u));
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody @Valid UserDto in){
        User u = userService.get(id);
        if(in.name()!=null) u.setName(in.name());
        if(in.email()!=null) u.setEmail(in.email());
        if(in.phone()!=null) u.setPhone(in.phone());
        return ApiMapper.toDto(userService.save(u));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        userService.delete(id);
    }
}
