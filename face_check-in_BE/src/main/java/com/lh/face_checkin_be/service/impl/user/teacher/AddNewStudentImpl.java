package com.lh.face_checkin_be.service.impl.user.teacher;

import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.pojo.Students;
import com.lh.face_checkin_be.service.user.teacher.AddNewStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:AddNewStudentImpl
 * Package:com.lh.face_checkin_be.service.impl.user.teacher
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/4/29-16:33
 * @version:v1.0
 */
@Service
public class AddNewStudentImpl implements AddNewStudent {
    @Autowired
    private StudentsMapper studentsMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Map<String, String> addNewStudent(String newId, String newUsername, String major, String schoolName) throws Exception {
        System.out.println(newId + " " + newUsername + " " + major + " " + schoolName);
        Map<String, String> map = new HashMap<>();
        Students student = studentsMapper.selectById(newId);
        if (student == null) {
            studentsMapper.insert(new Students(Integer.parseInt(newId),
                                    newUsername,
                                    schoolName,
                                    major,
                                    passwordEncoder.encode(newId),
                                    null));
            map.put("error_message", "success");
            return map;
        }
        if (student != null && !student.getUsername().equals(newUsername)) {
            map.put("error_message", "该学生姓名已存在, 请核对是否输入正确");
            return map;
        }
        if (student != null && student.getMajor() == null || student.getMajor().isEmpty()) {
            student.setMajor(major);
            studentsMapper.updateById(student);
            map.put("error_message", "success");
            return map;
        }
        if (student != null) {
            student.setMajor(major);
            studentsMapper.updateById(student);
            map.put("error_message", "success");
            return map;
        }
        return null;
    }
}
