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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

    private String checkFaceFeatures(String faceFeature) {
        FaceEngine faceEngine = engineInfo.getFaceEngine();
        // 获取数据库中所有学生的特征值
        List<Students> students = studentsMapper.selectList(null);
        System.out.println(students.size());
        FaceFeature targetFaceFeature = new FaceFeature(Base64.getDecoder().decode(faceFeature));

        int i = 0;
        for (Students student : students) {
            i++;
            System.out.println("i = " + i);
            if (student.getFaceFeatures() == null) continue;
            FaceFeature souceFaceFeature = new FaceFeature(Base64.getDecoder().decode(student.getFaceFeatures()));
            FaceSimilar faceSimilar = new FaceSimilar();
            int errorCode = faceEngine.compareFaceFeature(targetFaceFeature, souceFaceFeature, faceSimilar);
            System.out.println(student.getUsername() + "相似度：" + faceSimilar.getScore());
            if (faceSimilar.getScore() > 0.65) {
                System.out.println("识别成功" + student.getUsername());
                return student.getUsername();
            }
        }

        return "未识别成功, 请重试";
    }

    @Override
    public ResponseEntity<?> checkImage(String image) {
        image = image.replace("data:image/jpeg;base64,", "");
        base64ToImage(image);
        String faceFeature = getFaceFeatures(outputFilePath);

        return ResponseEntity.ok(checkFaceFeatures(faceFeature));
    }
}
