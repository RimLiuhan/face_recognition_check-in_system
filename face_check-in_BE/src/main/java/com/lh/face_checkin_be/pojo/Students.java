package com.lh.face_checkin_be.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName:Students
 * Package:com.lh.face_checkin_be.pojo
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/2-12:32
 * @version:v1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Students {
    @TableId(value = "id")
    String id;
    String username;
    String schoolName;
    String major;
    String password;
    String faceFeatures;
}
