package com.lh.face_checkin_be.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lh.face_checkin_be.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName:UserMapper
 * Package:com.lh.face_checkin_be.mapper
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/2-18:02
 * @version:v1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
