package com.lh.face_checkin_be.service.user.account;

import java.util.Map;

/**
 * ClassName:RegisterService
 * Package:com.lh.face_checkin_be.service.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/6-17:13
 * @version:v1.0
 */
public interface RegisterService {
    Map<String, String> register(String username, String password, String confirmPassword);
}
