package com.tranvuong.be_e_commerce.Services;

import java.util.Optional;

import com.tranvuong.be_e_commerce.Entity.User;
import com.tranvuong.be_e_commerce.dto.request.LoginRequest;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

public interface UserService {
    // Tìm user theo ID
    public Optional<User> getUserById(String id) ;

    // Tìm user theo email
    public Optional<User> getUserByEmail(String email) ;

    // Lưu user mới (Đăng ký)
    public User saveUser(User user) ;

    // Xóa user
    public void deleteUser(String id) ;

    // Lấy tất cả User
    public ResponseData getAllUsers() ;

    // Cập nhật thông tin user
    public ResponseData updateUser(String id, User updatedUser);

    //Login
    public ResponseData login(LoginRequest request) ;
    
    // Gửi email đặt lại mật khẩu
    public void sendResetPasswordEmail(String email);

    // Đặt lại mật khẩu
    public void resetPassword(String token, String newPassword);

}