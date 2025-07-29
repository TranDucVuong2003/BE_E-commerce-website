package com.tranvuong.be_e_commerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tranvuong.be_e_commerce.Entity.Cart;
import com.tranvuong.be_e_commerce.Entity.User;

public interface CartRepository extends JpaRepository<Cart, String> {
    Cart findByUser(User user);
}

