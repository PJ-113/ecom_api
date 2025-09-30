package com.ecom2.api;



import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ecom2.api.dto.CartItemDto;
import com.ecom2.api.mapper.ApiMapper;
import com.ecom2.entity.User;
import com.ecom2.service.CartService;
import com.ecom2.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
@SecurityRequirement(name = "bearer-jwt")
public class CartRestController {

    private final CartService cartService;
    private final UserService userService;

    public CartRestController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }
    
    private User me(Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing principal");
        return userService.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @GetMapping
    public List<CartItemDto> items(Principal principal) {
        return cartService.items(me(principal)).stream().map(ApiMapper::toDto).toList();
    }

    @PostMapping("/add/{productId}")
    public void add(@PathVariable Long productId, @RequestParam(defaultValue="1") int qty, Principal principal){
        cartService.add(me(principal), productId, qty);
    }

    @PostMapping("/remove/{itemId}")
    public void remove(@PathVariable Long itemId, Principal principal){
        cartService.remove(me(principal), itemId);
    }

    @PostMapping("/clear")
    public void clear(Principal principal){
        cartService.clear(me(principal));
    }
}
