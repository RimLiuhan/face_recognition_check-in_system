package com.lh.face_checkin_be.controller.user.administrator;

import com.lh.face_checkin_be.mapper.CoursesMapper;
import com.lh.face_checkin_be.pojo.Courses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName:GetAllCoursesController
 * Package:com.lh.face_checkin_be.controller.user.administrator
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/5/13-21:36
 * @version:v1.0
 */
@RestController
public class GetAllCoursesController {
    @Autowired
    private CoursesMapper coursesMapper;

    @GetMapping("/administrator/courselist/")
    public List<Courses> getAllCourses() {
        return coursesMapper.selectList(null);
    }
}
