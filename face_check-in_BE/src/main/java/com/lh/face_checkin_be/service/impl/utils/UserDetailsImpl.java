package com.lh.face_checkin_be.service.impl.utils;

import com.lh.face_checkin_be.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * ClassName:UserDetailsImpl
 * Package:com.lh.backend.service.impl.utils
 * Description: 实现spring security的UserDetails接口, spring security中的用户概念有自己的一套规则,
 *              所以需要实现UserDetails接口, 并且重写其中的方法
 *
 * @author:LH寒酥
 * @create:2025/1/17-18:20
 * @version:v1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private User user; // 聚合模式, 将自己的user对象聚合进来
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Integer getUserType() {
        return user.getUserType();
    }

    public String getId() {
        return user.getId();
    }
}
