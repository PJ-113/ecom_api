package com.ecom2.api;



import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom2.api.dto.OrderDto;
import com.ecom2.api.mapper.ApiMapper;
import com.ecom2.entity.Order;
import com.ecom2.entity.User;
import com.ecom2.service.OrderService;
import com.ecom2.service.UserService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderRestController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderRestController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    private User me(Principal principal) {
        return userService.findByEmail(principal.getName()).orElseThrow();
    }

    @PostMapping("/checkout")
    public OrderDto checkout(Principal principal){
        Order o = orderService.checkout(me(principal));
        return ApiMapper.toDto(o);
    }

    @GetMapping
    public List<OrderDto> myOrders(Principal principal){
        return orderService.ofUser(me(principal)).stream().map(ApiMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public OrderDto detail(@PathVariable Long id){
        return ApiMapper.toDto(orderService.getWithItems(id));
    }

    // ---------- แอดมิน ----------
    @GetMapping("/admin/all")
    public List<OrderDto> allOrders(){
        return orderService.allCheckedOut().stream().map(ApiMapper::toDto).toList();
    }
}
