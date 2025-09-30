package com.ecom2.api.mapper;

import com.ecom2.api.dto.*;
import com.ecom2.entity.*;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class ApiMapper {

    public static ProductDto toDto(Product p, long stock){
        return new ProductDto(
                p.getId(), p.getName(), p.getDescription(),
                p.getPrice(), stock, p.getImagePath(), p.getActive()
        );
    }

    public static void apply(Product p, ProductUpsertReq d){
        if (d.name() != null)        p.setName(d.name());
        if (d.description() != null) p.setDescription(d.description());
        if (d.price() != null)       p.setPrice(d.price());
        if (d.imagePath() != null)   p.setImagePath(d.imagePath());
        if (d.active() != null)      p.setActive(d.active());
        
    }

    public static CartItemDto toDto(CartItem ci){
        var p = ci.getProduct();

        
        BigDecimal price = (p != null && p.getPrice() != null)
                ? p.getPrice()
                : BigDecimal.ZERO;

       
        int qty = ci.getQuantity();

        
        BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));

      
        String imageUrl = (p != null) ? p.getImagePath() : null;

        // return DTO
        return new CartItemDto(
                ci.getId(),
                (p != null ? p.getId() : null),
                (p != null ? p.getName() : null),
                qty,
                price,
                lineTotal,
                imageUrl
        );
    }



    public static OrderItemDto toDto(OrderItem oi){
        var codes = oi.getRedeemCodes() == null ? java.util.List.<String>of()
                : oi.getRedeemCodes().stream().map(RedeemCode::getCode).toList();
        return new OrderItemDto(
                oi.getProduct().getId(), oi.getProduct().getName(), oi.getQuantity(), oi.getPrice(), codes
        );
    }

    public static OrderDto toDto(Order o){
        return new OrderDto(
                o.getId(),
                o.getUser() != null ? o.getUser().getId() : null,
                o.getCreatedAt(),
                o.getTotal(),
                o.getStatus(),
                o.getItems()==null? java.util.List.of() :
                        o.getItems().stream().map(ApiMapper::toDto).collect(Collectors.toList())
        );
    }
    
    public static UserDto toDto(User u) {
        if (u == null) return null;

        String role = String.valueOf(u.getRole());
        String name  = (u.getProfile() != null) ? u.getProfile().getName() : null;
        String phone = (u.getProfile() != null) ? u.getProfile().getPhone() : null;

        return new UserDto(
            u.getId(),
            name,
            u.getEmail(),
            phone,
            role
        );
    }
}
