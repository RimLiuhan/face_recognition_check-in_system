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
import org.apache.poi.ss.usermodel.DataFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

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
    public Map<String, String> createNewCourse(String schoolName, String courseName, String className, String teacherId, MultipartFile file) throws Exception {
        QueryWrapper<Courses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course", courseName);
        queryWrapper.eq("school_name", schoolName);
        queryWrapper.eq("major", className);
        if (coursesMapper.selectOne(queryWrapper) != null) {
            Map<String, String> map = new HashMap<>();
            map.put("error_message", "该课程已存在");
            map.put("school", schoolName);
            map.put("major", className);
            return map;
        }

        String message = checkStudentExist(file);
        if (message != null) {
            String school = message.split(" ")[0];
            String major = message.split(" ")[1];
            if (!school.equals(schoolName) || !major.equals(className)) {
                Map<String, String> map = new HashMap<>();
                map.put("error_message", "该班学生信息已存在, 您所新建的课程其学校或班级名称与已注册的不一致, 请返回修改");
                map.put("school", school);
                map.put("major", major);
                return map;
            }
        }

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
        System.out.println("idCol: " + idCol + " nameCol: " + nameCol);

//        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//            double sid = sheet.getRow(i).getCell(idCol).getNumericCellValue();
//            if (studentsMapper.selectById(sid) != null) continue;
//            String sname = sheet.getRow(i).getCell(nameCol).getStringCellValue();
//            String password = passwordEncoder.encode(String.valueOf((int)sid));
//            Students student = new Students((int) sid, sname, schoolName, className, password, null);
//            studentsMapper.insert(student);
//        }
        DataFormatter formatter = new DataFormatter();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            // 读取学号（兼容文本/数字/科学计数法）
            String sid = formatter.formatCellValue(sheet.getRow(i).getCell(idCol)).trim();
            try {
                if (studentsMapper.selectById(sid) != null) continue;

                // 读取姓名
                String sname = formatter.formatCellValue(sheet.getRow(i).getCell(nameCol)).trim();
                String password = passwordEncoder.encode(sid);
                Students student = new Students(sid, sname, schoolName, className, password, null);
                studentsMapper.insert(student);
                System.out.println(student);
            } catch (NumberFormatException e) {
                System.err.println("无效的学号格式: " + sid);
                continue;
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        return map;
    }

    private String getTeacherName(String teacherId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", teacherId);
        User user = userMapper.selectOne(queryWrapper);
        return user.getUsername();
    }

    private String checkStudentExist(MultipartFile file) throws Exception {
        DataFormatter formatter = new DataFormatter();
        Map<String, Integer> map = new HashMap<>();
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        HSSFSheet sheet = workbook.getSheetAt(0);

        // 找出学号所在列的序号
        Integer idCol = 0;
        for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i ++) {
            if (sheet.getRow(0).getCell(i).toString().equals("学号")) {
                idCol = i;
                break;
            }
        }
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            String sid = formatter.formatCellValue(sheet.getRow(i).getCell(idCol)).trim();
            Students student = studentsMapper.selectById(sid);
            if (student != null) {
                String key = student.getSchoolName() + " " + student.getMajor();
                map.put(key, map.getOrDefault(key, 0) + 1);
            }
        }
        // 如果没有数据，返回 null
        if (map.isEmpty()) {
            return null;
        }
        // 找到出现次数最多的 schoolName + major
        String maxKey = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxKey = entry.getKey();
            }
        }

        return maxKey;
    }
}
