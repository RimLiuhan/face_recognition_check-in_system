package com.lh.face_checkin_be.service.user.teacher;

import com.lh.face_checkin_be.pojo.Students;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * ClassName:AddNewCourse
 * Package:com.lh.face_checkin_be.service.user.teacher
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/4/6-13:39
 * @version:v1.0
 */
public interface AddNewCourse {
    Map<String, String> createNewCourse(String schoolName, String courseName, String className, String teacherId, MultipartFile file) throws Exception;
}
