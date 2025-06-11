package com.tranvuong.be_e_commerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tranvuong.be_e_commerce.Entity.Cart;
import com.tranvuong.be_e_commerce.Repository.CartRepository;

@Service
public class CartService {
  @Autowired
  private CartRepository cartRepository;

  // Lấy giỏ hàng theo User ID
  public Cart getCartByUserId(String userId) {
    return cartRepository.findByUserId(userId);
  }

  // Lấy danh sách sản phẩm trong giỏ hàng
  // public List<CartItem> getCartItems(String cartId) {
  // return cartItemRepository.findByCartId(cartId);
  // }

  // // Lưu giỏ hàng
  // public Cart saveCart(Cart cart) {
  // return cartRepository.save(cart);
  // }

  // // Thêm sản phẩm vào giỏ hàng
  // public CartItem addCartItem(CartItem cartItem) {
  // return cartItemRepository.save(cartItem);
  // }
}
