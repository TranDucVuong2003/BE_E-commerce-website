package com.tranvuong.be_e_commerce.Services.Impl;

import com.tranvuong.be_e_commerce.Entity.Cart;
import com.tranvuong.be_e_commerce.Entity.CartItem;
import com.tranvuong.be_e_commerce.Entity.Product;
import com.tranvuong.be_e_commerce.Entity.User;
import com.tranvuong.be_e_commerce.Repository.CartRepository;
import com.tranvuong.be_e_commerce.Repository.CartItemRepository;
import com.tranvuong.be_e_commerce.Repository.UserRepository;
import com.tranvuong.be_e_commerce.Repository.ProductRepository;
import com.tranvuong.be_e_commerce.Services.CartService;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    public ResponseData getCartByUserId(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseData("User not found", 404, 404, null);
        }
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            // Tạo mới cart nếu chưa có
            cart = new Cart();
            cart.setUser(user);
            cart.setCreated_at(LocalDate.now());
            cart = cartRepository.save(cart);
        }
        return new ResponseData("Get Cart Successfully", 200, 200, cart);
    }

    public ResponseData addCartItem(String cartId, String productId, int quantity, double price, String size) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);
        if (cart == null || product == null)
            return new ResponseData("Cart or product not found", 404, 404, null);

        // Kiểm tra sản phẩm đã có trong giỏ chưa (theo cả size)
        CartItem existingItem = cartItemRepository.findByCartAndProductAndSize(cart, product, size);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
            return new ResponseData("Updated existing item quantity", 200, 200, existingItem);
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(price);
        cartItem.setSize(size);
        cartItem.setCreated_at(LocalDate.now());

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        return new ResponseData("Add CartItem successfully", 200, 200, savedCartItem);
    }

    public ResponseData removeCartItem(String cartItemId) {
        cartItemRepository.deleteById(cartItemId);
        return new ResponseData("Delete Cart Item Successfully", 200, 200, null);
    }

    public ResponseData updateCartItemQuantity(String cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
        if (cartItem == null)
            return new ResponseData("Cart not found", 404, 404, null);
        cartItem.setQuantity(quantity);
        CartItem updatedCartItemQuantity = cartItemRepository.save(cartItem);
        return new ResponseData("Update CartItem successfully", 200, 200, updatedCartItemQuantity);
    }

    @Override
    public ResponseData getCartItems(String cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null)
            return new ResponseData("Get Cart Items", 200, 200, java.util.Collections.emptyList());
        return new ResponseData("Get Cart Items", 200, 200, cartItemRepository.findByCart(cart));
    }

    @Transactional
    @Override
    public ResponseData clearCart(String cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null)
            return new ResponseData("Cart not found", 404, 404, null);

        cartItemRepository.deleteAllByCart(cart);
        return new ResponseData("Cleared cart", 200, 200, null);
    }

}
