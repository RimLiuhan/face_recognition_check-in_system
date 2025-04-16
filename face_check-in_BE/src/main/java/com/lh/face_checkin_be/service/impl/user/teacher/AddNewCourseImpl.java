package com.lh.face_checkin_be.service.impl.user.teacher;

import com.lh.face_checkin_be.pojo.Students;
import com.lh.face_checkin_be.service.user.teacher.AddNewCourse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
    private SqlSessionFactory sqlSessionFactory;
    @Override
    public List<Students> getStudentsList(String schoolName, String courseName, String className, String teacherId,MultipartFile file) throws Exception {
        System.out.println(schoolName + " " + courseName + " " + className);
        List<Students> students = new ArrayList<>();
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        HSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i ++) {
            System.out.print(sheet.getRow(0).getCell(i) + " ");
        }
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            System.out.print(sheet.getRow(i).getCell(0) + " ");
            System.out.print(sheet.getRow(i).getCell(1) + " ") ;
            System.out.print(sheet.getRow(i).getCell(2) + " ");
            System.out.println();
        }
        return students;
    }
}
