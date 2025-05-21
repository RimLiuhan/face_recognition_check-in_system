package com.lh.face_checkin_be.controller.upload;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.toolkit.ImageInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.face_checkin_be.config.face_engine.EngineInfo;
import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.pojo.Students;
import com.lh.face_checkin_be.pojo.User;
import com.lh.face_checkin_be.proxy.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

/**
 * ClassName:TakePhotoController
 * Package:com.lh.face_checkin_be.controller.upload
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/4/28-13:12
 * @version:v1.0
 */
@RestController
public class TakePhotoController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    EngineInfo engineInfo;
    @Autowired
    StudentsMapper studentsMapper;
    private List<Students> students;

    @PostMapping("/student/takephoto/")
    @CurrentUser
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file,
                                         @RequestParam("id") String id,
                                         User user) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("请选择要上传的文件");
        }

        try {
            // 确保上传目录存在
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // 保存文件
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            String faceFeatures = getFaceFeatures(filePath.toString());
            if (faceFeatures == null) {
                return ResponseEntity.badRequest().body("请重新拍照, 人脸清晰离镜头近一点的照片");
            }
            // 查找students表, 对应ID更新face_features字段
            Map<String, String> map = checkFaceFeatures(faceFeatures);
            if (map.get("error_message").equals("success")) {
                String studentId = map.get("id");
                if (!studentId.equals(id))
                    return ResponseEntity.badRequest().body("请重新拍照, 确保是本人");
            }
            String studentId = user.getId();
            QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", studentId);
            Students students = studentsMapper.selectOne(queryWrapper);
            if (students != null) {
                students.setFaceFeatures(faceFeatures);
                studentsMapper.updateById(students);
            }
            // 返回响应
            Map<String, String> response = new HashMap<>();
            response.put("message", "文件上传成功");
            response.put("filename", newFilename);
            response.put("path", filePath.toString());
            response.put("studentId", String.valueOf(studentId));
            Files.delete(filePath);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("文件上传失败: " + e.getMessage());
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
        if (faceInfoList.isEmpty()) {
            return null;
        }

        // 特征提取
        FaceFeature faceFeature = new FaceFeature();
        errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);
        System.out.println("特征值大小：" + faceFeature.getFeatureData().length);
        return Base64.getEncoder().encodeToString(faceFeature.getFeatureData());
    }

//    private Integer getStudentId(User user) {
//        QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("username", user.getUsername());
//        return studentsMapper.selectOne(queryWrapper).getId();
//    }

    private Map<String, String> checkFaceFeatures(String faceFeature) {
        FaceEngine faceEngine = engineInfo.getFaceEngine();
        students = studentsMapper.selectList(null);

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
                map.put("id", String.valueOf(student.getId()));
                return map;
            }
        }

        map.put("error_message", "fail");
        return map;
    }
}
