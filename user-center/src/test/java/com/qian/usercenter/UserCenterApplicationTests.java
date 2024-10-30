package com.qian.usercenter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qian.usercenter.comment.AlgorithmUtil;
import com.qian.usercenter.comment.ErrorCode;
import com.qian.usercenter.job.exception.BusinessException;
import com.qian.usercenter.mapper.UserMapper;
import com.qian.usercenter.model.domain.User;
import com.qian.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
class UserCenterApplicationTests {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    RedissonClient redissonClient;
    @Resource
    RedisTemplate redisTemplate;
    //@Test
    /*void contextLoads() {
        String userAccount ="qian";
        String userPassword = "123456";
        String checkPassword = "123456";
        long Resulst = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(-1,Resulst);
         userAccount ="qian@";
         userPassword = "123456";
        long Resulst1 = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(-1,Resulst1);
        userPassword = "123456";
        checkPassword = "1234567";
        long Resulst11 = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(-1,Resulst11);
        userAccount ="qian";
        userPassword = "123456";
        checkPassword = "123456";
        long Resulst111 = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertTrue(Resulst111>0);
    }*/
    /*@Test
    void test1(){
       *//* String SALT = "qian";
        String encryptuserPassword = DigestUtils.md5DigestAsHex((SALT+"123456").getBytes());
        System.out.println(encryptuserPassword);*//*
        String userAccount ="qian";
        String userPassword = "123456";
        String checkPassword = "123456";
        long Resulst = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(-1,Resulst);
        userAccount = "!!#@#";
        long Resulst1 = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(-1,Resulst1);
    }*/
   /* @Test
    void searchUsersByTags() {
        *//*List<String> list = Arrays.asList("java","c++");
        List<User> userList = userService.searchUsersByTags(list);
        for (User user:userList
             ) {
            System.out.println(user.getUserAccount());
        }*//*
        //Assertions.assertNotNull(userList);
      *//*  ArrayList<String> string = new ArrayList<>();
        string.add("a");
        string.add("b");
        string.add("c");
        for (String word:string
             ) {
            System.out.println(word);
        }
        string.stream().forEach(s ->{
            System.out.println(s);
        });*//*
        LocalDate now = LocalDate.now();
        System.out.println(now);
        LocalTime now1 = LocalTime.now();
        System.out.println(now1);
        LocalTime now2 = LocalTime.now().withNano(0);
        System.out.println(now2);
        LocalDateTime now3 = LocalDateTime.now().minusNanos(0);
        System.out.println(now3);
        LocalDate of = LocalDate.of(2024, 3, 17);
        System.out.println(of);
    }*/
   /* @Resource
    RedisTemplate redisTemplate;
    @Test
    void test() {
    ValueOperations valueOperations = redisTemplate.opsForValue();
    valueOperations.set("fruit","orange");
}*/
    @Test
    void test() {
        List<Long> ImportantUserId = Arrays.asList(1L);
        RLock lock = redissonClient.getLock("redisson:precahe:lock");
        try {
            lock.tryLock(0,-1, TimeUnit.MILLISECONDS);
            for (Long id: ImportantUserId) {
                String userKey = String.format("yupao:usercenter:recommend:%s", id);
                ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                Page<User> page = userService.page(new Page<>(1, 8), userQueryWrapper);
                try{
                    stringObjectValueOperations.set(userKey,page,30000, TimeUnit.MILLISECONDS);
                }catch (Exception e) {
                    log.error("预热缓存存储错误",e);
                }
            }
        } catch (InterruptedException e) {
            log.error("error",e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    @Test
    public void matchUsers() {
        User loginUser = new User();
        loginUser.setTags("java");
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("id","tags");
        userQueryWrapper.isNotNull("tags");
        List<User> list = userService.list(userQueryWrapper);
        String loginUserTags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tags1 = gson.fromJson(loginUserTags, new TypeToken<List<String>>() {
        }.getType());
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据为空");
        }
        SortedMap<Long,Integer> objectObjectTreeMap = new TreeMap<>();
        ArrayList<Object> objects = new ArrayList<>();
        list.stream().map(a -> {
            List<String> userTagsList = gson.fromJson(a.getTags(), new TypeToken<List<String>>(){
            }.getType());
            Long id = a.getId();
            int i = AlgorithmUtil.minDistance(tags1, userTagsList);
            objectObjectTreeMap.put(id,i);
            return objectObjectTreeMap;
        });
        System.out.println(list);
    }
    @Test
    void m() {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        User user = userMapper.selectById(6);
        String tags = user.getTags();
        System.out.println(tags);
    }
}


