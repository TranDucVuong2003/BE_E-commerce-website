package com.tranvuong.be_e_commerce.Config;

import com.tranvuong.be_e_commerce.Security.JwtAuthenticationFilter;
import com.tranvuong.be_e_commerce.Security.JwtUtil;

import org.springframework.web.filter.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // Tắt CSRF
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Thêm cấu hình CORS
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/auth/**", "/files/**").permitAll() // Cho phép đăng ký và đăng nhập và file ảnh ko cần xác thực
            .anyRequest().authenticated() // Yêu cầu xác thực cho các request khác
        )
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // Đảm bảo filter JWT chạy trước UsernamePasswordAuthenticationFilter

    return http.build(); // Đảm bảo trả về SecurityFilterChain
}

@Bean
public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true); // Cho phép gửi thông tin xác thực (cookies, headers)
    config.addAllowedOriginPattern("*"); // Cho phép tất cả các nguồn (có thể thay bằng domain cụ thể)
    config.addAllowedHeader("*"); // Cho phép tất cả các headers
    config.addAllowedMethod("*"); // Cho phép tất cả các phương thức (GET, POST, PUT, DELETE, ...)
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
}

@Bean
public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOriginPattern("*"); // Thay "*" bằng domain cụ thể nếu cần
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", config);
    return source;
}

@Bean
public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
}

@Bean
public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Sử dụng BCrypt để mã hóa mật khẩu
    }
}