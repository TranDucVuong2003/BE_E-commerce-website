package com.tranvuong.be_e_commerce.Controller;

import com.tranvuong.be_e_commerce.Entity.Cart;
import com.tranvuong.be_e_commerce.Services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Lấy giỏ hàng theo User ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable String userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cart);
    }

    // Cập nhật giỏ hàng (nếu cần thêm logic)
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateCart(@PathVariable String userId, @RequestBody Cart updatedCart) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        cart.setTotal_price(updatedCart.getTotal_price());
        cart.setCreated_at(updatedCart.getCreated_at());
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }
}