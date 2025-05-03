package com.lh.face_checkin_be.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lh.face_checkin_be.pojo.Students;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * ClassName:StudentsMapper
 * Package:com.lh.face_checkin_be.mapper
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/2-12:35
 * @version:v1.0
 */
@Mapper
public interface StudentsMapper extends BaseMapper<Students> {
    @Update("UPDATE students SET major = NULL WHERE id = #{id}")
    int clearMajorById(@Param("id") Integer id);
}
