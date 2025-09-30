package com.ecom2.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecom2.entity.Order;
import com.ecom2.entity.User;
import com.ecom2.service.OrderService;
import com.ecom2.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/api/payment")
public class PaymentRestController {

  private final UserService userService;
  private final OrderService orderService;

  public PaymentRestController(UserService userService, OrderService orderService) {
    this.userService = userService;
    this.orderService = orderService;
  }

  private User currentUser(Principal principal) {
    if (principal == null) return null;
    return userService.getByEmail(principal.getName());
  }

  @PostMapping("/confirm")
  public ResponseEntity<?> confirm(@RequestBody PaymentRequest req, Principal principal) {
    var u = currentUser(principal);
    if (u == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login");

    if (!"card".equalsIgnoreCase(req.getMethod())) {
      return ResponseEntity.badRequest().body("Only card is supported");
    }

    try {
      Order order = orderService.checkout(u);
      // ส่งเฉพาะข้อมูลที่หน้าเว็บต้องใช้ก็พอ
      return ResponseEntity.ok(new PaymentResponse(order.getId(), order.getTotal()));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Payment failed: " + ex.getMessage());
    }
  }

  // DTOs
  public static class PaymentRequest {
    private String method;     
    private String cardNumber;
    private String cardName;
    private String expMonth;
    private String expYear;
    private String cvv;
    // getters/setters
    public String getMethod() { return method; } public void setMethod(String method){this.method=method;}
    public String getCardNumber(){return cardNumber;} public void setCardNumber(String x){this.cardNumber=x;}
    public String getCardName(){return cardName;} public void setCardName(String x){this.cardName=x;}
    public String getExpMonth(){return expMonth;} public void setExpMonth(String x){this.expMonth=x;}
    public String getExpYear(){return expYear;} public void setExpYear(String x){this.expYear=x;}
    public String getCvv(){return cvv;} public void setCvv(String x){this.cvv=x;}
  }
  public static class PaymentResponse {
    private Long orderId;
    private java.math.BigDecimal total;
    public PaymentResponse(Long id, java.math.BigDecimal total){ this.orderId=id; this.total=total; }
    public Long getOrderId(){return orderId;} public java.math.BigDecimal getTotal(){return total;}
  }
}

