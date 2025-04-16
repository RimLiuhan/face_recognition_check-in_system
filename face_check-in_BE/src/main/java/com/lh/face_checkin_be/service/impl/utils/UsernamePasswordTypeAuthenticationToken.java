package com.lh.face_checkin_be.service.impl.utils;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UsernamePasswordTypeAuthenticationToken extends AbstractAuthenticationToken {
    
    private final Object principal;
    private Object credentials;
    private final Integer userType;
    private final String id; // 学生ID字段

    public UsernamePasswordTypeAuthenticationToken(Object principal, Object credentials,
                                                   Integer userType, String id, Collection<? extends GrantedAuthority> authorities) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.userType = userType;
        this.id = id;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public Integer getUserType() {
        return userType;
    }

    public String getId() {
        return id;
    }
}