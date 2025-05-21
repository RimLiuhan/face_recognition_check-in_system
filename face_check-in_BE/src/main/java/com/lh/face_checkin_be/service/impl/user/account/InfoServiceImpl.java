package com.lh.face_checkin_be.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.pojo.Students;
import com.lh.face_checkin_be.pojo.User;
import com.lh.face_checkin_be.service.impl.utils.UserDetailsImpl;
import com.lh.face_checkin_be.service.user.account.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:InfoServiceImpl
 * Package:com.lh.face_checkin_be.service.impl.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/3-15:49
 * @version:v1.0
 */
@Service
public class InfoServiceImpl implements InfoService {
    @Autowired
    private StudentsMapper studentsMapper;
    @Override
    public Map<String, String> getInfo(User user) {
        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("usertype", String.valueOf(user.getUserType()));
        return map;
    }

    @Override
    public Map<String, String> checkFaceFeatures(User user) {
        QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", user.getId());
        Students students = studentsMapper.selectOne(queryWrapper);

        Map<String, String> map = new HashMap<>();
        if (students.getFaceFeatures() == null || students.getFaceFeatures().length() == 0) {
            map.put("error_message", "false");
        } else {
            map.put("error_message", "true");
        }
        return map;
    }
}
