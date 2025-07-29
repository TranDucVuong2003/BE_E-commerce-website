package com.tranvuong.be_e_commerce.Controller;

import com.tranvuong.be_e_commerce.Entity.User;
import com.tranvuong.be_e_commerce.Security.JwtUtil;
import com.tranvuong.be_e_commerce.Services.Impl.UserServiceImpl;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Lấy tất cả người dùng
    @GetMapping
    public ResponseEntity<ResponseData> getAllUsers() {
        ResponseData users = userService.getAllUsers();
        return ResponseEntity.status(users.getStatusCode()).body(users);
    }

    // Lấy thông tin người dùng theo ID
    @GetMapping("/id/{id}")
    public ResponseEntity<ResponseData> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(new ResponseData("User found", 200, 0, user)))
                .orElse(ResponseEntity.status(404).body(new ResponseData("User not found", 404, 1001, null)));
    }

    // Lấy thông tin người dùng theo email
    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseData> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(new ResponseData("Successful", 200, 200, user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Lấy thông tin người dùng đang đăng nhập
    @GetMapping("/my-profile")
    public ResponseEntity<ResponseData> getMyProfile(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        String userId = jwtUtil.extractUserId(token);   // Lấy userId từ subject
        String email = jwtUtil.extractEmail(token);     // Lấy email từ claim "email"
    
        // Lấy thông tin user từ userId (hoặc email)
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(new ResponseData("Success", 200, 0, userOpt.get()));
        } else {
            return ResponseEntity.status(404).body(new ResponseData("User not found", 404, 1001, null));
        }
    }

    // Tạo người dùng mới
    @PostMapping
    public ResponseEntity<ResponseData> createUser(@RequestBody User user) {
        ResponseData savedUser = userService.createUser(user);
        return ResponseEntity.status(savedUser.getStatusCode()).body(savedUser);
    }

    // Xóa người dùng theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> deleteUser(@PathVariable String id) {
        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ResponseData deletedUser = userService.deleteUser(id);
        return ResponseEntity.status(deletedUser.getStatusCode()).body(deletedUser);
    }

    // Cập nhật thông tin người dùng theo ID
    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        ResponseData response = userService.updateUser(id, updatedUser);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}