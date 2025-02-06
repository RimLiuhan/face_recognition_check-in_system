package com.lh.face_checkin_be.controller.upload;

/**
 * ClassName:FileUploadController
 * Package:com.lh.face_checkin_be.controller.upload
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/3-17:27
 * @version:v1.0
 */
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.face_checkin_be.config.face_engine.EngineInfo;
import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.pojo.Students;
import com.lh.face_checkin_be.pojo.User;
import com.lh.face_checkin_be.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

@RestController
public class FileUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    EngineInfo engineInfo;

    @Autowired
    StudentsMapper studentsMapper;

    @PostMapping("/upload/")
    public ResponseEntity<String> handleFileUpload(@RequestParam("photo") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        try {
            // 确保上传目录存在
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 保存文件
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadDir + file.getOriginalFilename());
            Files.write(path, bytes);

            int studentId = getStudentId();
            System.out.println("studentId: " + studentId);
            String faceFeatures = getFaceFeatures(path.toString());
            // 查找students表, 对应ID更新face_features字段
            QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", studentId);
            Students students = studentsMapper.selectOne(queryWrapper);
            if (students != null) {
                students.setFaceFeatures(faceFeatures);
                studentsMapper.updateById(students);
            }



            return ResponseEntity.ok("File saved: " + path.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to save file: " + e.getMessage());
        }

    }

    private Integer getStudentId() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        return user.getId();
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
}