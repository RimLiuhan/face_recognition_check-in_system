package com.lh.face_checkin_be.controller.user.teacher;

import com.lh.face_checkin_be.service.user.teacher.AddNewStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName:AddNewStudentController
 * Package:com.lh.face_checkin_be.controller.user.teacher
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/4/29-16:36
 * @version:v1.0
 */
@RestController
public class AddNewStudentController {
    @Autowired
    private AddNewStudent addNewStudent;
    @PostMapping("/addNewStudent/")
    public Map<String, String> addNewStudent(@RequestParam String newId,
                                             @RequestParam String newUsername,
                                             @RequestParam String major,
                                             @RequestParam String schoolName) throws Exception {
        return addNewStudent.addNewStudent(newId, newUsername, major, schoolName);
    }
}
