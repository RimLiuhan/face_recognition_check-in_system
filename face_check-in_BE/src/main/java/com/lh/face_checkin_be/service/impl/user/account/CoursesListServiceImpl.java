package com.lh.face_checkin_be.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lh.face_checkin_be.mapper.CoursesMapper;
import com.lh.face_checkin_be.pojo.Courses;
import com.lh.face_checkin_be.pojo.User;
import com.lh.face_checkin_be.service.impl.utils.UserDetailsImpl;
import com.lh.face_checkin_be.service.user.account.CoursesListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName:CoursesListServiceImpl
 * Package:com.lh.face_checkin_be.service.impl.user.account
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/7-16:22
 * @version:v1.0
 */
@Service
public class CoursesListServiceImpl implements CoursesListService {
    @Autowired
    private CoursesMapper coursesMapper;

    @Override
    public List<Courses> getCoursesList(User user) {
        String username = user.getUsername();
        QueryWrapper<Courses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher", username);
        List<Courses> courses = coursesMapper.selectList(queryWrapper);

        return courses;
    }
}
