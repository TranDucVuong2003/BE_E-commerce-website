package com.tranvuong.be_e_commerce.Entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Tự động tạo ID dạng UUID
    private String id;
    private String name;
    private String email;
    private String password;
    private String refreshToken;

    @Enumerated(EnumType.STRING) // Lưu dưới dạng String thay vì số
    @Column(nullable = false)
    private Role role;

    private LocalDate created_at;
    
    // No-args constructor
    public User() {
    }

    // All-args constructor
    public User(String id, String name, String email, String password, String refreshToken, Role role, LocalDate created_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.refreshToken = refreshToken;
        this.role = role;
        this.created_at = created_at;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDate getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }

    // toString method
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", role=" + role +
                ", created_at=" + created_at +
                '}';
    }

    // Equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (refreshToken != null ? !refreshToken.equals(user.refreshToken) : user.refreshToken != null) return false;
        if (role != user.role) return false;
        return created_at != null ? created_at.equals(user.created_at) : user.created_at == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (refreshToken != null ? refreshToken.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (created_at != null ? created_at.hashCode() : 0);
        return result;
    }

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDate.now(); // Gán giá trị ngày hiện tại
    }
}