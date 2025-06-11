package com.tranvuong.be_e_commerce.Security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    public JwtAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        setAuthenticated(true); // Đánh dấu là đã xác thực
    }

    @Override
    public Object getCredentials() {
        return null; // Không cần lưu mật khẩu
    }

    @Override
    public Object getPrincipal() {
        return principal; // Email của người dùng
    }
}
