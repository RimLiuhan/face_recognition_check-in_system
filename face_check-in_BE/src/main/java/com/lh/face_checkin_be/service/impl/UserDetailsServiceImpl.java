package com.lh.face_checkin_be.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.face_checkin_be.mapper.UserMapper;
import com.lh.face_checkin_be.pojo.User;
import com.lh.face_checkin_be.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ClassName:UserDetailsServiceImpl
 * Package:com.lh.backend.service.impl
 * Description: 用来接入数据库信息, 同样还是实现Spring security 的 UserDetailsService接口
 *             聚合模式
 *
 * @author:LH寒酥
 * @create:2025/1/17-17:52
 * @version:v1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return new UserDetailsImpl(user);
    }
}
