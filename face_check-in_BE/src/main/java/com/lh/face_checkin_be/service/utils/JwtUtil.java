package com.lh.face_checkin_be.service.utils;

/**
 * ClassName:JwtUtil
 * Package:com.lh.backend.utils
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/1/19-15:21
 * @version:v1.0
 */
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    // Token有效期 - 14天
    public static final long JWT_TTL = 60 * 60 * 1000L * 24 * 14;
    // 密钥
    public static final String JWT_KEY = "SDFGjhdsfalshdfHFdsjkdsfds121232131afasdfac";
    // 用户类型声明名称
    public static final String CLAIM_USER_TYPE = "userType";

    /**
     * 生成UUID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 创建JWT（包含用户类型）
     * @param subject 用户ID
     * @param userType 用户类型（1-学生，2-教师，3-管理员）
     * @return JWT字符串
     */
    public static String createJWT(String subject, Integer userType) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID(), userType);
        System.out.println("生成 JWT 的学号：" + subject);
        return builder.compact();
    }

    /**
     * 创建JWT构建器
     */
    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid, Integer userType) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 设置过期时间
        if (ttlMillis == null) {
            ttlMillis = JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);

        // 构建JWT
        return Jwts.builder()
                .setId(uuid)
                .setSubject(subject)  // 用户ID
                .claim(CLAIM_USER_TYPE, userType)  // 添加用户类型声明
                .setIssuer("sg")
                .setIssuedAt(now)
                .signWith(secretKey, signatureAlgorithm)
                .setExpiration(expDate);
    }

    /**
     * 生成加密密钥
     */
    public static SecretKey generalKey() {
        byte[] encodeKey = Base64.getDecoder().decode(JWT_KEY);
        return new SecretKeySpec(encodeKey, 0, encodeKey.length, "HmacSHA256");
    }

    /**
     * 解析JWT
     */
    public static Claims parseJWT(String jwt) throws JwtException {
        SecretKey secretKey = generalKey();
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * 从JWT中获取用户类型
     */
    public static Integer getUserTypeFromToken(String jwt) {
        try {
            Claims claims = parseJWT(jwt);
            return claims.get(CLAIM_USER_TYPE, Integer.class);
        } catch (Exception e) {
            throw new JwtException("获取用户类型失败", e);
        }
    }

    /**
     * 从JWT中获取用户ID
     */
    public static String getUserIdFromToken(String jwt) {
        try {
            Claims claims = parseJWT(jwt);
            return claims.getSubject();
        } catch (Exception e) {
            throw new JwtException("获取用户ID失败", e);
        }
    }
}