package com.tranvuong.be_e_commerce.Controller;

import com.tranvuong.be_e_commerce.Entity.User;
import com.tranvuong.be_e_commerce.Services.Impl.UserServiceImpl;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    // Lấy tất cả người dùng
    @GetMapping
    public ResponseEntity<ResponseData> getAllUsers() {
        ResponseData users = userService.getAllUsers();
        return ResponseEntity.ok(users);
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

    // Tạo người dùng mới
    @PostMapping
    public ResponseEntity<ResponseData> createUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(new ResponseData("User created successfully", 200, 200, savedUser));
    }

    // Xóa người dùng theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }

    // Cập nhật thông tin người dùng theo ID
    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
    ResponseData response = userService.updateUser(id, updatedUser);
    return ResponseEntity.status(response.getStatusCode()).body(response);
}
}