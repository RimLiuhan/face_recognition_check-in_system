package com.lh.face_checkin_be.controller.user.teacher;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.toolkit.ImageInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lh.face_checkin_be.config.face_engine.EngineInfo;
import com.lh.face_checkin_be.mapper.StudentsMapper;
import com.lh.face_checkin_be.pojo.Students;
import org.springframework.beans.factory.ObjectFactory;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

/**
 * ClassName:UploadPictureCheckIn
 * Package:com.lh.face_checkin_be.controller.user.teacher
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/5/3-14:00
 * @version:v1.0
 */
@RestController
public class UploadPictureCheckIn {
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private StudentsMapper studentsMapper;
    @Autowired
    private ObjectFactory<EngineInfo> engineInfoProvider; // 使用ObjectFactory延迟获取

    private List<Students> students;
    private EngineInfo engineInfo;

    @PostMapping("/uploadPictureCheckIn/")
    public List<String> uploadPictureCheckIn(@RequestParam("photo") MultipartFile file,
                                             @RequestParam("major") String major,
                                             @RequestParam("schoolName") String schoolName) {
        System.out.println("major:" + major + " schoolName:" + schoolName);
        engineInfo = engineInfoProvider.getObject();
        List<String> sname = new ArrayList<>();
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
            getStudentsByMajor(major, schoolName);


            List<Students> studentsInPicture = studentsInPicture(path.toString());
            for (Students student : studentsInPicture) {
                System.out.println(student);
                sname.add(student.getUsername());
            }

//            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sname;
    }

    public void getStudentsByMajor(String major, String school_name) {
        QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("major", major);
        queryWrapper.eq("school_name", school_name);
        students = studentsMapper.selectList(queryWrapper);
    }

    private List<Students> studentsInPicture(String imagePath) {
        List<Students> list = new ArrayList<>();
        int errorCode = engineInfo.getErrorCode();
        FaceEngine faceEngine = engineInfo.getFaceEngine();
        //人脸检测
        ImageInfo imageInfo = getRGBData(new File(imagePath));
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        System.out.println(faceInfoList.size());
        if (faceInfoList.isEmpty()) {
            return null;
        }

        for (int i = 0; i < faceInfoList.size(); i++) {
            FaceFeature targetFaceFeature = new FaceFeature();
            errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(i), targetFaceFeature);
            for (Students student : students) {
                if (student.getFaceFeatures() == null) continue;
                FaceFeature souceFaceFeature = new FaceFeature(Base64.getDecoder().decode(student.getFaceFeatures()));
                FaceSimilar faceSimilar = new FaceSimilar();
                errorCode = faceEngine.compareFaceFeature(targetFaceFeature, souceFaceFeature, faceSimilar);
                System.out.println(student.getUsername() + "相似度：" + faceSimilar.getScore());
                if (faceSimilar.getScore() > 0.65) {
                    System.out.println("识别成功" + student.getUsername());
                    list.add(student);
                    break;
                }
            }
        }

        if (faceEngine != null)
            faceEngine.unInit();
        return list;
    }
}
