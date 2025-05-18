package com.lh.face_checkin_be.controller.user.account;

import com.lh.face_checkin_be.service.user.account.UpdatePasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:UpdatePasswordController
 * Package:com.lh.face_checkin_be.controller.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/5/18-10:47
 * @version:v1.0
 */
@RestController
public class UpdatePasswordController {
    @Autowired
    private UpdatePasswordService updatePasswordService;
    @PostMapping("/student/updatepassword/")
    public ResponseEntity<?> updatePassword(@RequestParam String id,
                                            @RequestParam String password,
                                            @RequestParam String confirmedPassword){
        return updatePasswordService.updatePassword(id, password, confirmedPassword);
    }
}
