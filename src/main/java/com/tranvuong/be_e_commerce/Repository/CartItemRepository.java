package com.tranvuong.be_e_commerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tranvuong.be_e_commerce.Entity.CartItem;
import com.tranvuong.be_e_commerce.Entity.Cart;
import com.tranvuong.be_e_commerce.Entity.Product;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    List<CartItem> findByCart(Cart cart);
    CartItem findByCartAndProduct(Cart cart, Product product);
    void deleteAllByCart(Cart cart);
}
