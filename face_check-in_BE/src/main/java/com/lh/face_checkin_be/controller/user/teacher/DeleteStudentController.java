package com.lh.face_checkin_be.controller.user.teacher;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.pojo.Students;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:DeleteStudentController
 * Package:com.lh.face_checkin_be.controller.user.teacher
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/4/29-17:55
 * @version:v1.0
 */
@RestController
public class DeleteStudentController {
    @Autowired
    private StudentsMapper studentsMapper;
    @PostMapping("/deleteStudent/")
    @Transactional
    public Map<String, String> deleteStudent(@RequestParam String id) throws Exception{
        int result = studentsMapper.clearMajorById(Integer.parseInt(id));
        Map<String, String> map = new HashMap<>();
        if (result > 0) map.put("error_message", "success");
        else map.put("error_message", "fail");
        return map;
    }
}
