package com.lh.face_checkin_be.controller.user.account;

import com.lh.face_checkin_be.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName:RegisterController
 * Package:com.lh.backend.controller.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/1/20-16:58
 * @version:v1.0
 */
@RestController
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    @PostMapping("/user/account/register/")
    public Map<String, String> register(@RequestParam Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        String confirmPassword = map.get("confirmPassword");
        return registerService.register(username, password, confirmPassword);
    }
}
