package com.tranvuong.be_e_commerce.Controller;

import com.tranvuong.be_e_commerce.Services.Impl.CartServiceImpl;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartServiceImpl cartService;

    // 1. Lấy giỏ hàng của user
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseData> getCartByUserId(@PathVariable String userId) {
        ResponseData user = cartService.getCartByUserId(userId);
        return ResponseEntity.status(user.getStatusCode()).body(user);
    }

    // 2. Thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    public ResponseEntity<ResponseData> addCartItem(
            @RequestParam String cartId,
            @RequestParam String productId,
            @RequestParam int quantity,
            @RequestParam double price) {
        ResponseData addCart = cartService.addCartItem(cartId, productId, quantity, price);
        return ResponseEntity.status(addCart.getStatusCode()).body(addCart);
    }

    // 3. Cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/update")
    public ResponseEntity<ResponseData> updateCartItemQuantity(
            @RequestParam String cartItemId,
            @RequestParam int quantity) {
        ResponseData updatedCartItem = cartService.updateCartItemQuantity(cartItemId, quantity);
        return ResponseEntity.status(updatedCartItem.getStatusCode()).body(updatedCartItem);
    }

    // 4. Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<ResponseData> removeCartItem(@PathVariable String id) {
        ResponseData deleted = cartService.removeCartItem(id);
        return ResponseEntity.status(deleted.getStatusCode()).body(deleted);
    }

    // 5. Lấy danh sách sản phẩm trong giỏ hàng
    @GetMapping("/items/{cartId}")
    public ResponseEntity<ResponseData> getCartItems(@PathVariable String cartId) {
        ResponseData getAllItem = cartService.getCartItems(cartId);
        return ResponseEntity.status(getAllItem.getStatusCode()).body(getAllItem);
    }

    @DeleteMapping("/clear/{cartId}")
    public ResponseEntity<ResponseData> clearCart(@PathVariable String cartId) {
        ResponseData cleared = cartService.clearCart(cartId);
        return ResponseEntity.status(cleared.getStatusCode()).body(cleared);
    }
}