package com.lh.face_checkin_be;

import com.arcsoft.face.*;
import com.arcsoft.face.toolkit.ImageInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lh.face_checkin_be.config.face_engine.EngineInfo;
import com.lh.face_checkin_be.mapper.CoursesMapper;
import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.pojo.Courses;
import com.lh.face_checkin_be.pojo.Students;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

@SpringBootTest
class FaceCheckInBeApplicationTests {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EngineInfo engineInfo;

    @Autowired
    CoursesMapper coursesMapper;

    @Autowired
    StudentsMapper studentsMapper;

    @Test
    void contextLoads() {
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("101"));
    }
    @Test
    void test() {

        int errorCode = engineInfo.getErrorCode();
        FaceEngine faceEngine = engineInfo.getFaceEngine();
        System.out.println(errorCode);
        //人脸检测
        ImageInfo imageInfo = getRGBData(new File("src/main/resources/static/photo.png"));
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        System.out.println(faceInfoList);

//        // 特征提取
//        FaceFeature faceFeature = new FaceFeature();
//        errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);
//        System.out.println("特征值大小：" + faceFeature.getFeatureData().length);

//        // 设置活体测试
//        errorCode = faceEngine.setLivenessParam(0.5f, 0.7f);
//        //人脸属性检测
//        FunctionConfiguration configuration = new FunctionConfiguration();
//        configuration.setSupportAge(true);
//        configuration.setSupportFace3dAngle(true);
//        configuration.setSupportGender(true);
//        configuration.setSupportLiveness(true);
//        errorCode = faceEngine.process(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList, configuration);
//
//
//        //性别检测
//        List<GenderInfo> genderInfoList = new ArrayList<GenderInfo>();
//        errorCode = faceEngine.getGender(genderInfoList);
//        System.out.println("性别：" + genderInfoList.get(0).getGender());
//
//        //年龄检测
//        List<AgeInfo> ageInfoList = new ArrayList<AgeInfo>();
//        errorCode = faceEngine.getAge(ageInfoList);
//        System.out.println("年龄：" + ageInfoList.get(0).getAge());
    }
    @Test
    void test2() {
        QueryWrapper<Courses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher", "野兽先辈");
        List<Courses> courses = coursesMapper.selectList(queryWrapper);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println(objectMapper.writeValueAsString(courses));
            System.out.println(ResponseEntity.ok(courses));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test3() {
        List<Students> list = studentsMapper.selectList(null);
        for (Students students : list) {
            System.out.println(students.getUsername());
        }
    }
}
