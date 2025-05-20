package com.lh.face_checkin_be.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.pojo.Students;
import com.lh.face_checkin_be.service.user.account.UpdatePasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:UpdatePasswordServiceImpl
 * Package:com.lh.face_checkin_be.service.impl.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/5/18-10:43
 * @version:v1.0
 */
@Service
public class UpdatePasswordServiceImpl implements UpdatePasswordService {
    @Autowired
    private StudentsMapper studentsMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public ResponseEntity<?> updatePassword(String studentId, String newPassword, String newConfirmedPassword) {
        int id = Integer.parseInt(studentId);
        System.out.println(id + " " + newPassword + " " + newConfirmedPassword);
        Map<String, Object> map = new HashMap<>();
        if (newPassword == null || newConfirmedPassword == null) {
            map.put("success", false);
            map.put("message", "密码, 确认密码不能为空");
            return ResponseEntity.badRequest().body(map);
        }
        if (newPassword.length() > 100 || newConfirmedPassword.length() > 100) {
            map.put("success", false);
            map.put("message", "密码长度不能大于100");
            return ResponseEntity.badRequest().body(map);
        }
        if (newPassword.length() == 0 || newConfirmedPassword.length() == 0) {
            map.put("success", false);
            map.put("message", "密码, 确认密码不能为空");
            return ResponseEntity.badRequest().body(map);
        }
        if (!newPassword.equals(newConfirmedPassword)) {
            map.put("success", false);
            map.put("message", "两次密码输入不一致");
            return ResponseEntity.badRequest().body(map);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Students students = studentsMapper.selectOne(queryWrapper);
        students.setPassword(encodedPassword);
        studentsMapper.update(students, queryWrapper);
        map.put("success", true);
        map.put("message", "密码修改成功");
        return ResponseEntity.ok()
                .body(map);
    }
}
