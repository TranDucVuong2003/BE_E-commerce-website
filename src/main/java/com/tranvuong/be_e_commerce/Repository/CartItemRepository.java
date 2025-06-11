package com.tranvuong.be_e_commerce.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tranvuong.be_e_commerce.Entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    List<CartItem> findByCart_Id(String cartId);

}
