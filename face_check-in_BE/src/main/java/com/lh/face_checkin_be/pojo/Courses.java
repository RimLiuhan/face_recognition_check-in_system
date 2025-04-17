package com.lh.face_checkin_be.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName:Courses
 * Package:com.lh.face_checkin_be.pojo
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/7-16:13
 * @version:v1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Courses {
    private String course;
    private String schoolName;
    private String major;
    private String teacher;

//    @Override
//    public String toString() {
//        return "[course:" + course + ", major:" + major + ", teacher:" + teacher + "]";
//    }
}
