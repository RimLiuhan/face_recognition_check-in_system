package com.lh.face_checkin_be.service.user.account;

import com.lh.face_checkin_be.pojo.Courses;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * ClassName:CoursesListService
 * Package:com.lh.face_checkin_be.service.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/7-16:21
 * @version:v1.0
 */
public interface CoursesListService {
    public List<Courses> getCoursesList();
}
