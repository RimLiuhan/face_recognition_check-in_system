package com.lh.face_checkin_be.proxy;

import com.lh.face_checkin_be.pojo.User;
import com.lh.face_checkin_be.service.impl.utils.UserDetailsImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * ClassName:CurrentUserAspect
 * Package:com.lh.face_checkin_be.proxy
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/16-15:11
 * @version:v1.0
 */
@Component
@Aspect
public class CurrentUserAspect {

    @Around("@annotation(CurrentUser)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 从 SecurityContextHolder 中获取当前用户
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // 目标方法传来的参数中如果有 User 类型的参数，则替换为当前用户
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof User) {
                args[i] = user; // 替换为当前用户
            }
        }

        // 继续执行目标方法
        return joinPoint.proceed(args);
    }
}
