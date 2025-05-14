package com.lh.face_checkin_be.controller.user.administrator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.face_checkin_be.mapper.CoursesMapper;
import com.lh.face_checkin_be.pojo.Courses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:DeleteCourseController
 * Package:com.lh.face_checkin_be.controller.user.administrator
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/5/14-10:37
 * @version:v1.0
 */
@RestController
public class DeleteCourseController {
    @Autowired
    private CoursesMapper coursesMapper;
    @PostMapping("/administrator/deleteCourse/")
    @Transactional
    public ResponseEntity<?> deleteCourse(@RequestParam String course,
                                          @RequestParam String schoolName,
                                          @RequestParam String major,
                                          @RequestParam String teacher) {
        QueryWrapper<Courses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course", course);
        queryWrapper.eq("school_name", schoolName);
        queryWrapper.eq("major", major);
        queryWrapper.eq("teacher", teacher);
        coursesMapper.delete(queryWrapper);
        return ResponseEntity.ok("success");
    }
}
