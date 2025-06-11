package com.tranvuong.be_e_commerce.Security;

import java.text.ParseException;
import java.util.Date;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWTClaimsSet;
import com.tranvuong.be_e_commerce.Entity.Role;

import lombok.extern.slf4j.Slf4j;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;

import org.springframework.beans.factory.annotation.Value;



@Component
@Slf4j
public class JwtUtil {

     @Value("${jwt.refresh-token-expiration-time}")
     private long REFRESH_TOKEN_EXPIRATION_TIME;

     @Value("${jwt.secret-key}")
     private String SECRET_KEY;

     @Value("${jwt.expiration-time}")
     private long EXPIRATION_TIME;

     // Tạo token từ email
     public String generateAccessToken(String email, Role role) {
          try {
               JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

               JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                         .subject(email)
                         .issuer("tranvuong.com")
                         .issueTime(new Date())
                         .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                         .claim("role", role.name()) // Thêm role vào token
                         .build();

               Payload payload = new Payload(claimsSet.toJSONObject());
               JWSObject jwsObject = new JWSObject(header, payload);

               jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));

               return jwsObject.serialize();
          } catch (Exception e) {
               throw new RuntimeException("Lỗi khi tạo token", e);
          }
     }

     // Tạo refresh token từ email
     public String generateRefreshToken(String email) {
          try {
              JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
              JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                      .subject(email)
                      .issuer("tranvuong.com")
                      .issueTime(new Date())
                      .expirationTime(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                      .build();
      
              Payload payload = new Payload(claimsSet.toJSONObject());
              JWSObject jwsObject = new JWSObject(header, payload);
              jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
      
              return jwsObject.serialize();
          } catch (Exception e) {
              throw new RuntimeException("Lỗi tạo Refresh Token", e);
          }
      }
      

     // Trích xuất email từ token
     public String extractEmail(String token) {
          try {
               JWSObject jwsObject = JWSObject.parse(token);
               JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
               return claimsSet.getSubject();
          } catch (ParseException e) {
               log.error("Lỗi khi trích xuất email từ token", e);
               throw new RuntimeException("Lỗi khi trích xuất email từ token", e);
          }
     }

     public String extractRole(String token) {
          try {
              JWSObject jwsObject = JWSObject.parse(token);
              JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
              return claimsSet.getStringClaim("role");  // Lấy role từ token
          } catch (Exception e) {
              throw new RuntimeException("Lỗi khi trích xuất role từ token", e);
          }
      }

     // Kiểm tra token hợp lệ
     public boolean validateToken(String token) {
          try {
               JWSObject jwsObject = JWSObject.parse(token);

               // Kiểm tra chữ ký
               if (!jwsObject.verify(new MACVerifier(SECRET_KEY.getBytes()))) {
                    System.out.println("Token không hợp lệ!");
                    return false;
               }

               // Kiểm tra thời gian hết hạn
               JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
               Date expiration = claimsSet.getExpirationTime();

               if (expiration.before(new Date())) {
                    System.out.println("Token đã hết hạn!");
                    return false;
               }

               return true;
          } catch (Exception e) {
               System.out.println("Lỗi khi kiểm tra token!");
               return false;
          }
     }

     public List<SimpleGrantedAuthority> getAuthorities(String role) {
         return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
     }
}
