package com.lh.face_checkin_be.controller.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.pojo.Students;
import com.lh.face_checkin_be.service.upload.CameraPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName:GetAllStudents
 * Package:com.lh.face_checkin_be.controller.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/8-20:22
 * @version:v1.0
 */
@RestController
public class GetAllStudents {
    @Autowired
    private CameraPictureService cameraPictureService;

    @GetMapping("/getAllStudents/")
    public List<String> getAllStudents(@RequestParam String major) {

        return cameraPictureService.getStudentsByMajor(major);
    }
}
