package com.lh.face_checkin_be.service.impl.user.teacher;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.face_checkin_be.mapper.CoursesMapper;
import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.mapper.UserMapper;
import com.lh.face_checkin_be.pojo.Courses;
import com.lh.face_checkin_be.pojo.Students;
import com.lh.face_checkin_be.pojo.User;
import com.lh.face_checkin_be.service.user.teacher.AddNewCourse;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName:AddNewCourseImpl
 * Package:com.lh.face_checkin_be.service.impl.user.teacher
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/4/6-15:33
 * @version:v1.0
 */
@Service
public class AddNewCourseImpl implements AddNewCourse {
    @Autowired
    private StudentsMapper studentsMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CoursesMapper coursesMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    @Transactional
    public boolean createNewCourse(String schoolName, String courseName, String className, String teacherId, MultipartFile file) throws Exception {
        System.out.println(schoolName + " " + courseName + " " + className + " " + teacherId);
        String teacherName = getTeacherName(teacherId);
        coursesMapper.insert(new Courses(courseName, schoolName, className, teacherName));

        Integer idCol = 0, nameCol = 0;
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        HSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i ++) {
            if (sheet.getRow(0).getCell(i).toString().equals("学号")) {
                idCol = i;
            } else if (sheet.getRow(0).getCell(i).toString().equals("姓名")) {
                nameCol = i;
            }
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            double sid = sheet.getRow(i).getCell(idCol).getNumericCellValue();
            if (studentsMapper.selectById(sid) != null) continue;
            String sname = sheet.getRow(i).getCell(nameCol).getStringCellValue();
            String password = passwordEncoder.encode(String.valueOf((int)sid));
            Students student = new Students((int) sid, sname, schoolName, className, password, null);
            studentsMapper.insert(student);
        }
        return true;
    }

    private String getTeacherName(String teacherId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", teacherId);
        User user = userMapper.selectOne(queryWrapper);
        return user.getUsername();
    }
}
