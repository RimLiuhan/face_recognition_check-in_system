package com.lh.face_checkin_be.service.user.account;

import org.springframework.http.ResponseEntity;

/**
 * ClassName:UpdatePasswordService
 * Package:com.lh.face_checkin_be.service.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/5/18-10:42
 * @version:v1.0
 */
public interface UpdatePasswordService {
    public ResponseEntity<?> updatePassword(String studentId, String newPassword, String newConfirmedPassword);
}
