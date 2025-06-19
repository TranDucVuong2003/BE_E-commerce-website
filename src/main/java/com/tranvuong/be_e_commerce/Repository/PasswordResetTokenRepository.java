package com.tranvuong.be_e_commerce.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tranvuong.be_e_commerce.Entity.PasswordResetToken;
import com.tranvuong.be_e_commerce.Entity.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    void deleteByUser(User user);
    List<PasswordResetToken> findByUser(User user); 
}
