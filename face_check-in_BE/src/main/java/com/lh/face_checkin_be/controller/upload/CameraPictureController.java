package com.lh.face_checkin_be.controller.upload;

import com.lh.face_checkin_be.service.upload.CameraPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName:CameraPictureController
 * Package:com.lh.face_checkin_be.controller.upload
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/7-19:43
 * @version:v1.0
 */
@RestController
public class CameraPictureController {
    @Autowired
    private CameraPictureService cameraPictureService;
    @PostMapping("/camera/")
    public ResponseEntity<?> uploadImage(@RequestBody Map<String, String> request) {
        try {
            // 检查请求体是否为空
            if (request == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("请求体不能为空");
            }

            // 检查图像数据是否为空
            String imageData = request.get("image");
            if (imageData == null || imageData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("图像数据不能为空");
            }

            // 模拟处理图像数据
            System.out.println("收到图像数据：" + imageData.substring(0, 50) + "...");

            // 返回成功响应
            return cameraPictureService.checkImage(imageData);
        } catch (Exception e) {
            // 捕获所有异常，返回 500 错误
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("未检测到人脸, 请重试");
        }
    }
}
