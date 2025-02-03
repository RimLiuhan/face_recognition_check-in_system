package com.lh.face_checkin_be.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName:User
 * Package:com.lh.face_checkin_be.pojo
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/2-18:00
 * @version:v1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String userType;
}
