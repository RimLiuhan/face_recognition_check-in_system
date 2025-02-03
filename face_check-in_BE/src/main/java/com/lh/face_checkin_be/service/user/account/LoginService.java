package com.lh.face_checkin_be.service.user.account;

import java.util.Map;

/**
 * ClassName:LoginService
 * Package:com.lh.face_checkin_be.service.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/2-12:36
 * @version:v1.0
 */
public interface LoginService {
    public Map<String, String> getToken(Integer usertype, String username, String password);
}
