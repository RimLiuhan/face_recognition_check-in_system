package com.lh.face_checkin_be.controller.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.pojo.Students;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName:GetAllStudentsInfoController
 * Package:com.lh.face_checkin_be.controller.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/4/29-15:26
 * @version:v1.0
 */
@RestController
public class GetAllStudentsInfoController {
    @Autowired
    private StudentsMapper studentsMapper;
    @GetMapping("/getAllStudentsInfo/")
    public List<Students> getAllStudentsInfo(@RequestParam String major,
                                             @RequestParam String schoolName){
        QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("major", major);
        queryWrapper.eq("school_name", schoolName);
        return studentsMapper.selectList(queryWrapper);
    }
}
