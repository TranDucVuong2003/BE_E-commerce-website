package com.tranvuong.be_e_commerce.Services;

import com.tranvuong.be_e_commerce.dto.response.ResponseData;



public interface CartService {
    ResponseData getCartByUserId(String userId);
    ResponseData addCartItem(String cartId, String productId, int quantity, double price);
    ResponseData removeCartItem(String cartItemId);
    ResponseData updateCartItemQuantity(String cartItemId, int quantity);
    ResponseData getCartItems(String cartId);
    ResponseData clearCart(String cartId);
}
