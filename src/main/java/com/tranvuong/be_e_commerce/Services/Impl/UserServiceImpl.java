package com.tranvuong.be_e_commerce.Services.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tranvuong.be_e_commerce.Entity.PasswordResetToken;
import com.tranvuong.be_e_commerce.Entity.User;
import com.tranvuong.be_e_commerce.Repository.PasswordResetTokenRepository;
import com.tranvuong.be_e_commerce.Repository.UserRepository;
import com.tranvuong.be_e_commerce.Security.JwtUtil;
import com.tranvuong.be_e_commerce.Services.UserService;
import com.tranvuong.be_e_commerce.dto.request.LoginRequest;
import com.tranvuong.be_e_commerce.dto.response.AuthResponse;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    // private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

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

    
    @Override
    @Transactional
    public void sendResetPasswordEmail(String email) {
        try {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

            // Xóa tất cả token cũ của user này
            List<PasswordResetToken> oldTokens = passwordResetTokenRepository.findByUser(user);
            if (!oldTokens.isEmpty()) {
                passwordResetTokenRepository.deleteAll(oldTokens);
                passwordResetTokenRepository.flush(); // Đảm bảo xóa ngay lập tức
            }

            // Tạo token mới với UUID khác
            String token = UUID.randomUUID().toString();
            
            // Kiểm tra token mới không trùng với token cũ
            while (passwordResetTokenRepository.findByToken(token) != null) {
                token = UUID.randomUUID().toString();
            }

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
            
            // Lưu token mới
            passwordResetTokenRepository.saveAndFlush(resetToken);

            // Gửi email
            String resetLink = frontendUrl + "/reset-password?token=" + token;
            sendEmail(user.getEmail(), resetLink);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to send reset password email: " + e.getMessage());
        }
    }

    private void sendEmail(String toEmail, String link) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            // message.setFrom("tranvuong3101@gmail.com");  // Thêm email người gửi
            message.setTo(toEmail);
            message.setSubject("Reset Password");
            message.setText("Click the link to reset your password: " + link);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token is invalid or expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }

}
