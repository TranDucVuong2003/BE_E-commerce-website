package com.tranvuong.be_e_commerce.Services.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tranvuong.be_e_commerce.Entity.User;
import com.tranvuong.be_e_commerce.Repository.UserRepository;
import com.tranvuong.be_e_commerce.Security.JwtUtil;
import com.tranvuong.be_e_commerce.Services.UserService;
import com.tranvuong.be_e_commerce.dto.request.LoginRequest;
import com.tranvuong.be_e_commerce.dto.response.AuthResponse;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

@Service
public class UserServiceImpl implements UserService {

    // private final PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override // Dùng @Override để ghi đè phương thức từ interface
    // Tìm user theo ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    // Tìm user theo email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    // Lưu user mới (Đăng ký)
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // Đảm bảo rollback nếu có lỗi xảy ra
    // Xóa user
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    // Lấy tất cả User
    public ResponseData getAllUsers() {
        return new ResponseData("Success", 200, 1000, userRepository.findAll());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseData updateUser(String id, User updatedUser) {
        try {
            Optional<User> existingUserOpt = userRepository.findById(id);

            // 1. Kiểm tra xem giá trị có trống không
            if (existingUserOpt.isEmpty()) {
                return new ResponseData("User not found with id: " + id, 404, 404, null);
            }

            User existingUser = existingUserOpt.get();

            // 2. Validate dữ liệu đầu vào
            if (updatedUser == null) {
                return new ResponseData("Update data cannot be null", 400, 400, null);
            }

            // 3. Kiểm tra mail mới (sau khi thay đổi) có bị trùng không
            if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
                Optional<User> userWithSameEmail = userRepository.findByEmail(updatedUser.getEmail());
                if (userWithSameEmail.isPresent()) {
                    return new ResponseData("Email already exists", 400, 400, null);
                }
            }

            // 4. Cập nhật thông tin
            boolean hasChanges = false;

            if (updatedUser.getName() != null && !updatedUser.getName().trim().isEmpty()) {
                existingUser.setName(updatedUser.getName().trim());
                hasChanges = true;
            }

            if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
                existingUser.setEmail(updatedUser.getEmail().trim());
                hasChanges = true;
            }

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
                // Kiểm tra độ dài mật khẩu
                if (updatedUser.getPassword().length() < 6) {
                    return new ResponseData(
                            "Password must be at least 6 characters long",
                            400,
                            400,
                            null);
                }
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                hasChanges = true;
            }

            if (updatedUser.getRole() != null) {
                existingUser.setRole(updatedUser.getRole());
                hasChanges = true;
            }
            // 5. Kiểm tra xem có thay đổi nào không
            if (!hasChanges) {
                return new ResponseData(
                        "No changes detected",
                        200,
                        200,
                        existingUser);
            }

            // 6. Lưu thay đổi
            User savedUser = userRepository.save(existingUser);

            // 7. Trả về kết quả thành công
            return new ResponseData(
                    "User updated successfully",
                    200,
                    200,
                    savedUser);

        } catch (Exception e) {
            return new ResponseData(
                    "Error updating user: " + e.getMessage(),
                    500,
                    500,
                    null);
        }
    }

    @Override
    // Login
    public ResponseData login(LoginRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isEmpty()) {
            return new ResponseData("Invalid email or password.", 401, 1000, null);
        }

        User foundUser = existingUser.get();
        if (!passwordEncoder.matches(request.getPassword(), foundUser.getPassword())) {
            return new ResponseData("Invalid email or password.", 401, 1000, null);
        }

        // Tạo JWT token kèm role
        String token = jwtUtil.generateAccessToken(foundUser.getEmail(), foundUser.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(foundUser.getEmail());

        return new ResponseData("Login successful.", 200, 0,
                new AuthResponse(refreshToken, token, foundUser.getRole().name()));
    }
}
