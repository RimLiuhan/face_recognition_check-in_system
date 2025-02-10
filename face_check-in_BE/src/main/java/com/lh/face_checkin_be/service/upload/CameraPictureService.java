package com.lh.face_checkin_be.service.upload;

import com.lh.face_checkin_be.pojo.Students;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * ClassName:CameraPictureService
 * Package:com.lh.face_checkin_be.service.upload
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/8-16:52
 * @version:v1.0
 */
public interface CameraPictureService {
    ResponseEntity<?> checkImage(String image);
    List<String> getStudentsByMajor(String major);
}
