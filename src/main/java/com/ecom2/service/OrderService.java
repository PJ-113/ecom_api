package com.ecom2.service;

import com.ecom2.entity.CartItem;
import com.ecom2.entity.Order;
import com.ecom2.entity.OrderItem;
import com.ecom2.entity.Product;
import com.ecom2.entity.RedeemCode;
import com.ecom2.entity.User;
import com.ecom2.repo.OrderItemRepository;
import com.ecom2.repo.OrderRepository;
import com.ecom2.repo.ProductRepository;
import com.ecom2.repo.RedeemCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final CartService cartService;
    private final ProductRepository productRepo;

    // ใช้ RedeemCode เป็นแหล่งความจริงเรื่องสต็อก
    private final RedeemCodeService redeemCodeService;
    private final RedeemCodeRepository redeemCodeRepo;

    public OrderService(OrderRepository orderRepo,
                        OrderItemRepository orderItemRepo,
                        CartService cartService,
                        ProductRepository productRepo,
                        RedeemCodeService redeemCodeService,
                        RedeemCodeRepository redeemCodeRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.cartService = cartService;
        this.productRepo = productRepo;
        this.redeemCodeService = redeemCodeService;
        this.redeemCodeRepo = redeemCodeRepo;
    }

    /**
     * Checkout แบบ mock payment:
     * - ตรวจสต็อกจากจำนวน RedeemCode ที่ AVAILABLE
     * - สร้าง Order/OrderItem
     * - จ่าย RedeemCode ให้แต่ละ OrderItem และ mark เป็น ASSIGNED
     * - เคลียร์ตะกร้า
     */
    @Transactional
    public Order checkout(User user) {

        // 1) ดึงตะกร้า
        List<CartItem> cartItems = cartService.items(user);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Cart empty");
        }

        // 2) โหลด Product สด ๆ
        List<Long> productIds = cartItems.stream()
                .map(ci -> ci.getProduct().getId())
                .distinct()
                .toList();

        Map<Long, Product> freshProducts = productRepo.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 3) ตรวจปริมาณ & สต็อกจาก RedeemCode
        for (CartItem ci : cartItems) {
            int qty = Math.max(0, ci.getQuantity());
            if (qty <= 0) {
                throw new IllegalArgumentException("Invalid quantity for product: " + ci.getProduct().getName());
            }
            Product p = freshProducts.get(ci.getProduct().getId());
            if (p == null) {
                throw new IllegalStateException("Product not found: " + ci.getProduct().getId());
            }
            long stock = redeemCodeService.availableStock(p);
            if (stock < qty) {
                throw new IllegalStateException("Not enough stock for " + p.getName() + " (left " + stock + ")");
            }
        }

        // 4) สร้าง Order แล้วบันทึกให้มี id ทันที (managed)
        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PAID");                 // ถ้าใช้ String
        order.setTotal(BigDecimal.ZERO);
        order = orderRepo.saveAndFlush(order);   // สำคัญ: ให้มี id ก่อน

        // 5) สร้างและบันทึก OrderItem ทั้งหมดก่อน (ยังไม่จองโค้ด)
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> itemsToSave = new ArrayList<>();

        for (CartItem ci : cartItems) {
            Product p = freshProducts.get(ci.getProduct().getId());

            OrderItem oi = new OrderItem();
            oi.setOrder(order);                  // order managed แล้ว
            oi.setProduct(p);
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(p.getPrice());
            itemsToSave.add(oi);

            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        itemsToSave = orderItemRepo.saveAll(itemsToSave);
        orderItemRepo.flush();                   // กันค้างคิวรี

        // 6) จอง & ผูก & markAssigned โค้ด หลังจาก items ถูก insert แล้ว
        List<RedeemCode> allPicked = new ArrayList<>();
        for (OrderItem oi : itemsToSave) {
            List<RedeemCode> picked = redeemCodeService.allocateCodes(oi.getProduct(), oi.getQuantity());
            for (RedeemCode rc : picked) {
                rc.setOrderItem(oi);             // ผูกกับ oi ที่มี id แล้ว
            }
            allPicked.addAll(picked);
        }
        if (!allPicked.isEmpty()) {
            redeemCodeService.markAssigned(allPicked);  // saveAll โค้ด
        }

        // 7) อัปเดตรวมเงิน & บันทึก
        order.setTotal(total);
        order = orderRepo.saveAndFlush(order);

        // 8) เคลียร์ตะกร้า
        cartService.clear(user);

        // 9) โหลดกลับพร้อม items แล้วเติม codes แบบแยกคิวรี (หลบ MultipleBagFetch)
        return getWithItems(order.getId());
    }

    /** รายการออเดอร์ของผู้ใช้ที่เช็คเอาท์แล้ว */
    public List<Order> ofUser(User user) {
        return orderRepo.findCheckedOutByUser(user);
    }

    /** รายการออเดอร์ทั้งหมดที่เช็คเอาท์แล้ว */
    public List<Order> allCheckedOut() {
        return orderRepo.findAllCheckedOut();
    }

    /** ดึงออเดอร์พร้อม items (ใช้กับหน้า detail) */
    @Transactional(readOnly = true)
    public Order getWithItems(Long id) {
        return orderRepo.findWithItems(id).orElseThrow();
    }

    /** (ออปชัน) ดึงโค้ดของรายการสินค้าในออเดอร์ ไว้ใช้กรณีต้องแสดงทีละแถว */
    @Transactional(readOnly = true)
    public List<String> codesOfOrderItem(Long orderItemId) {
        return redeemCodeRepo.findAll().stream()
                .filter(rc -> rc.getOrderItem() != null && rc.getOrderItem().getId().equals(orderItemId))
                .map(RedeemCode::getCode)
                .toList();
    }
}


