package com.lh.face_checkin_be.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName:CurrentUser
 * Package:com.lh.face_checkin_be.proxy
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/16-15:10
 * @version:v1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}
