package com.lh.face_checkin_be.service.user.account;

import com.lh.face_checkin_be.pojo.User;

import java.util.Map;

/**
 * ClassName:InfoService
 * Package:com.lh.face_checkin_be.service.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/2-19:26
 * @version:v1.0
 */
public interface InfoService {
    public Map<String, String> getInfo(User user);
    Map<String, String> checkFaceFeatures(User user);
}
