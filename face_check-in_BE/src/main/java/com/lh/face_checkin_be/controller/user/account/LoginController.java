package com.lh.face_checkin_be.controller.user.account;

import com.lh.face_checkin_be.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName:LoginController
 * Package:com.lh.face_checkin_be.controller.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/2-11:01
 * @version:v1.0
 */
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/user/account/token/")
    public Map<String, String> login(@RequestParam Map<String,String> map){
        Integer usertype = Integer.parseInt(map.get("usertype"));
        String username = map.get("username");
        String password = map.get("password");
        String id = map.get("id");
        System.out.println(usertype+" "+username+" "+password+" "+id);

        return loginService.getToken(usertype, username, password, id);
    }
}
