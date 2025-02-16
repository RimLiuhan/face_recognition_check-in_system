package com.lh.face_checkin_be.controller.user.account;

import com.lh.face_checkin_be.pojo.User;
import com.lh.face_checkin_be.proxy.CurrentUser;
import com.lh.face_checkin_be.service.user.account.InfoService;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName:InfoServiceController
 * Package:com.lh.face_checkin_be.controller.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/3-15:51
 * @version:v1.0
 */
@RestController
public class InfoServiceController {
    @Autowired
    private InfoService infoService;

    @GetMapping("user/account/info/")
    @CurrentUser
    public Map<String, String> getInfo(User user)
    {
        return infoService.getInfo(user);
    }

    @GetMapping("user/account/checkFaceFeatures/")
    @CurrentUser
    public Map<String, String> checkFaceFeatures(User user)
    {
        return infoService.checkFaceFeatures(user);
    }
}
