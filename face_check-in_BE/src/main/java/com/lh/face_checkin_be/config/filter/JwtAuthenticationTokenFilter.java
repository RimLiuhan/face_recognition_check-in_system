package com.lh.face_checkin_be.config.filter;

import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.mapper.UserMapper;
import com.lh.face_checkin_be.pojo.Students;
import com.lh.face_checkin_be.pojo.User;
import com.lh.face_checkin_be.service.impl.utils.UserDetailsImpl;
import com.lh.face_checkin_be.service.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ClassName:JwtAuthenticationTokenFilter
 * Package:com.lh.backend.config.filter
 * Description: 验证jwt token
 *
 * @author:LH寒酥
 * @create:2025/1/19-15:24
 * @version:v1.0
 */

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentsMapper studentsMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        try {
            Claims claims = JwtUtil.parseJWT(token);
            String userId = claims.getSubject();
            Integer userType = claims.get("userType", Integer.class);

            // 根据用户类型查询不同表
            UserDetailsImpl loginUser;
            if (userType == 1) { // 学生
                Students student = studentsMapper.selectById(userId);
                if (student == null) {
                    throw new RuntimeException("学生不存在");
                }
                loginUser = new UserDetailsImpl(
                        new User(student.getId(), student.getUsername(), student.getPassword(), userType)
                );
            } else { // 教师或管理员
                User user = userMapper.selectById(userId);
                if (user == null) {
                    throw new RuntimeException("用户不存在");
                }
                loginUser = new UserDetailsImpl(user);
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (Exception e) {
            // 处理JWT解析失败的情况
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("无效的token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}