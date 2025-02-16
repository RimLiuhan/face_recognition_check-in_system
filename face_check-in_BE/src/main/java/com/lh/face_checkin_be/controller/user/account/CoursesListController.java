package com.lh.face_checkin_be.controller.user.account;

import com.lh.face_checkin_be.pojo.Courses;
import com.lh.face_checkin_be.pojo.User;
import com.lh.face_checkin_be.proxy.CurrentUser;
import com.lh.face_checkin_be.service.user.account.CoursesListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName:CoursesListController
 * Package:com.lh.face_checkin_be.controller.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/7-16:57
 * @version:v1.0
 */
@RestController
public class CoursesListController {
    @Autowired
    private CoursesListService coursesListService;

    @GetMapping("teacher/courselist/")
    @CurrentUser
    public List<Courses> getCoursesList(User user)
    {
        return coursesListService.getCoursesList(user);
    }
}
