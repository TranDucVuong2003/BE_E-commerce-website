package com.tranvuong.be_e_commerce.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.tranvuong.be_e_commerce.Entity.Cart;

public interface CartRepository extends JpaRepository<Cart, String> {
    Cart findByUserId(String userId);
}

