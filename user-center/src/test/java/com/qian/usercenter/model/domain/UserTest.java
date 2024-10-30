package com.qian.usercenter.model.domain;
import java.util.Date;

import com.qian.usercenter.mapper.UserMapper;
import com.qian.usercenter.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserTest {
    @Resource
    private UserService userService;
    @Test
    void add(){
        User user = new User();
        user.setUserName("qian");
        user.setUserAccount("xxx");
        user.setAvatarUrl("xx");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("xxx");
        user.setEmail("xxx");
        user.setUserStatus(0);
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);


    }

}