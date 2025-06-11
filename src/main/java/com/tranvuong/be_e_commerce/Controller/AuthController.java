package com.tranvuong.be_e_commerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.tranvuong.be_e_commerce.Entity.User;
import com.tranvuong.be_e_commerce.Entity.Role;
import com.tranvuong.be_e_commerce.Security.JwtUtil;
import com.tranvuong.be_e_commerce.Services.Impl.UserServiceImpl;
import com.tranvuong.be_e_commerce.dto.request.LoginRequest;
import com.tranvuong.be_e_commerce.dto.request.RefreshTokenRequest;
import com.tranvuong.be_e_commerce.dto.response.JwtResponse;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseData> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseData("Refresh Token không hợp lệ hoặc đã hết hạn", 401, 401, null));
        }

        String email = jwtUtil.extractEmail(refreshToken);
        Optional<User> optionalUser = userService.getUserByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData("Không tìm thấy người dùng", 404, 404, null));
        }

        User user = optionalUser.get();
        String newAccessToken = jwtUtil.generateAccessToken(email, user.getRole());

        return ResponseEntity.ok(new ResponseData("Refresh Token thành công", 200, 200, new JwtResponse(newAccessToken)));
    }

    // Đăng ký người dùng
    @PostMapping("/register")
    public ResponseEntity<ResponseData> registerUser(@RequestBody User user) {
        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Kiểm tra nếu email đã tồn tại
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ResponseData("Email already exists.", 400, 400, null));
        }

        // Set role mặc định là USER nếu role chưa được set
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        // Lưu người dùng mới vào cơ sở dữ liệu
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(new ResponseData("User registered successfully", 200, 200, savedUser));
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<ResponseData> loginUser(@RequestBody LoginRequest request) {
        return ResponseEntity.status(userService.login(request).getStatusCode())
        .body(userService.login(request)); 
    }
    // Đăng xuất người dùng
    @PostMapping("/logout")
    public ResponseEntity<ResponseData> logoutUser() {
        // Thường thì logout bằng cách xóa token hoặc xóa session trên phía client
        // Trong trường hợp này, việc xóa token trên server là không cần thiết vì JWT là stateless
        return ResponseEntity.ok(new ResponseData("Logout successful!", 200,200, null));
    }
}
