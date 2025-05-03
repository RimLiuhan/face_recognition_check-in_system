package com.lh.face_checkin_be.service.user.teacher;

import java.util.Map;

/**
 * ClassName:AddNewStudent
 * Package:com.lh.face_checkin_be.service.user.teacher
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/4/29-16:31
 * @version:v1.0
 */
public interface AddNewStudent {
    Map<String, String> addNewStudent(String newId, String newUsername, String major, String schoolName) throws Exception;
}
