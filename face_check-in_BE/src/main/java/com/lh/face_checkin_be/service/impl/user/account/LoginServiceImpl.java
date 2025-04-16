package com.lh.face_checkin_be.service.impl.user.account;

import com.lh.face_checkin_be.pojo.User;
import com.lh.face_checkin_be.service.impl.utils.UserDetailsImpl;
import com.lh.face_checkin_be.service.impl.utils.UsernamePasswordTypeAuthenticationToken;
import com.lh.face_checkin_be.service.user.account.LoginService;
import com.lh.face_checkin_be.service.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:LoginServiceImpl
 * Package:com.lh.face_checkin_be.service.impl.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/2-12:38
 * @version:v1.0
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Map<String, String> getToken(Integer usertype, String username, String password, String sid) {
        // 创建用户密码认证令牌
        UsernamePasswordTypeAuthenticationToken authenticationToken =
                new UsernamePasswordTypeAuthenticationToken(username, password, usertype, sid, null);
        // 用户身份验证
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 获取认证后的用户信息
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        User user = loginUser.getUser();

        //生成jwt令牌
        String jwt = JwtUtil.createJWT(user.getId().toString(), user.getUserType());

        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("token", jwt);
        return map;
    }
}
