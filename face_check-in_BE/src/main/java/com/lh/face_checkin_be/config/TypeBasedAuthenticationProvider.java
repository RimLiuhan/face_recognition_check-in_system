package com.lh.face_checkin_be.config;

import com.lh.face_checkin_be.service.impl.UserDetailsServiceImpl;
import com.lh.face_checkin_be.service.impl.utils.UserDetailsImpl;
import com.lh.face_checkin_be.service.impl.utils.UsernamePasswordTypeAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TypeBasedAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TypeBasedAuthenticationProvider(
            UserDetailsServiceImpl userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordTypeAuthenticationToken auth =
                (UsernamePasswordTypeAuthenticationToken) authentication;

        String username = auth.getName();
        String password = auth.getCredentials().toString();
        Integer userType = auth.getUserType();
        String id = auth.getId();

        try {
            // 使用自定义方法加载用户
            UserDetails userDetails = userDetailsService.loadUserByUsernameAndType(
                    userType, username, id);

            // 验证密码
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("用户名或密码错误");
            }

            // 验证用户类型是否匹配
            if (userDetails instanceof UserDetailsImpl) {
                UserDetailsImpl detailsImpl = (UserDetailsImpl) userDetails;
                if (!userType.equals(detailsImpl.getUserType())) {
                    throw new BadCredentialsException("用户类型不匹配");
                }
            }

            // 创建认证成功的token
            return new UsernamePasswordTypeAuthenticationToken(
                    userDetails,
                    null, // 认证后清除凭证
                    userType,
                    id,
                    userDetails.getAuthorities());

        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("用户不存在", e);
        } catch (Exception e) {
            throw new AuthenticationServiceException("认证过程出错", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordTypeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}