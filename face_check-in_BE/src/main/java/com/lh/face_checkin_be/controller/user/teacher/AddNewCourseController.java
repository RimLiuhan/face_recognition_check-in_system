package com.lh.face_checkin_be.controller.user.teacher;

import com.lh.face_checkin_be.pojo.Students;
import com.lh.face_checkin_be.service.user.teacher.AddNewCourse;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:AddNewCourseController
 * Package:com.lh.face_checkin_be.controller.user.teacher
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/4/6-13:44
 * @version:v1.0
 */
@RestController
public class AddNewCourseController {

    @Autowired
    private AddNewCourse addNewCourse;

    @RequestMapping("/course/add/")
    public ResponseEntity<String> addNewCourse(
            @RequestParam String schoolName,
            @RequestParam String courseName,
            @RequestParam String className,
            @RequestParam String teacherId,
            @RequestParam("studentList") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded -- 请上传学生名单");
        }
        try {
            boolean result = addNewCourse.createNewCourse(schoolName, courseName, className, teacherId, file);
            if (!result) {
                return ResponseEntity.badRequest().body("Failed to create new course -- 创建课程失败");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to read file -- 文件读取失败");
        }
        return ResponseEntity.ok("success -- 创建课程成功");
    }
}
