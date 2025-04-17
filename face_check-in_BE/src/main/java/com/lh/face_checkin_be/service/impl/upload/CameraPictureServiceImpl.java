package com.lh.face_checkin_be.service.impl.upload;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.toolkit.ImageInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.face_checkin_be.config.face_engine.EngineInfo;
import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.pojo.Students;
import com.lh.face_checkin_be.service.upload.CameraPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

/**
 * ClassName:CameraPictureServiceImpl
 * Package:com.lh.face_checkin_be.service.impl.upload
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/8-16:53
 * @version:v1.0
 */
@Service
public class CameraPictureServiceImpl implements CameraPictureService {
    @Autowired
    private StudentsMapper studentsMapper;
    @Autowired
    private EngineInfo engineInfo;
    private String outputFilePath = "src/main/resources/static/camera.jpg";
    private List<Students> students;

    public List<String> getStudentsByMajor(String major, String school_name) {
        QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("major", major);
        queryWrapper.eq("school_name", school_name);
        students = studentsMapper.selectList(queryWrapper);
        List<String> studentList = students.stream().map(Students::getUsername).collect(Collectors.toList());
        return studentList;
    }
    private void base64ToImage(String base64String) {
        //base64转图片
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64String);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);

            File outputFile = new File(outputFilePath);
            ImageIO.write(image, "jpg", outputFile);
            System.out.println("图片保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFaceFeatures(String imagePath) {
        int errorCode = engineInfo.getErrorCode();
        FaceEngine faceEngine = engineInfo.getFaceEngine();
        //人脸检测
        ImageInfo imageInfo = getRGBData(new File(imagePath));
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        System.out.println(faceInfoList);

        // 特征提取
        FaceFeature faceFeature = new FaceFeature();
        errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);
        System.out.println("特征值大小：" + faceFeature.getFeatureData().length);
        return Base64.getEncoder().encodeToString(faceFeature.getFeatureData());
    }

    private Map<String, String> checkFaceFeatures(String faceFeature) {
        FaceEngine faceEngine = engineInfo.getFaceEngine();

        System.out.println(students.size());
        FaceFeature targetFaceFeature = new FaceFeature(Base64.getDecoder().decode(faceFeature));
        HashMap<String, String> map = new HashMap<>();

        for (Students student : students) {
            if (student.getFaceFeatures() == null) continue;
            FaceFeature souceFaceFeature = new FaceFeature(Base64.getDecoder().decode(student.getFaceFeatures()));
            FaceSimilar faceSimilar = new FaceSimilar();
            int errorCode = faceEngine.compareFaceFeature(targetFaceFeature, souceFaceFeature, faceSimilar);
            System.out.println(student.getUsername() + "相似度：" + faceSimilar.getScore());
            if (faceSimilar.getScore() > 0.65) {
                System.out.println("识别成功" + student.getUsername());
                map.put("error_message", "success");
                map.put("username", student.getUsername());
                return map;
            }
        }

        map.put("error_message", "fail");
        return map;
    }

    @Override
    public ResponseEntity<?> checkImage(String image) {
        image = image.replace("data:image/jpeg;base64,", "");
        base64ToImage(image);
        String faceFeature = getFaceFeatures(outputFilePath);
        Map<String, String> map = checkFaceFeatures(faceFeature);
        if (map.get("error_message").equals("success")) {
            return ResponseEntity.ok(map.get("username"));
        }
        return ResponseEntity.status(500).body("未查询到人脸信息,请重试并确保上传过人脸信息");
    }
}
