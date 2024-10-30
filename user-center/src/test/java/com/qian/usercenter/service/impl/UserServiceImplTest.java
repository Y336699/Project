package com.qian.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qian.usercenter.mapper.UserMapper;
import com.qian.usercenter.model.domain.User;
import com.qian.usercenter.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceImplTest {
    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;
    @Test
    void eq() {
        User user = new User();
        user.setId(3l);
        user.setUserName("qian");
        user.setUserAccount("jingqian");
        user.setUserPassword("123456");
        user.setUserStatus(0);
        user.setUserRole(1);
        userMapper.insert(user);
        String username = "qian";
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("username",username);
        System.out.println(userService.list(wrapper));
    }

}