package com.qian.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qian.usercenter.comment.BaseResponse;
import com.qian.usercenter.comment.ErrorCode;
import com.qian.usercenter.comment.ResultUtils;
import com.qian.usercenter.job.exception.BusinessException;
import com.qian.usercenter.model.domain.User;
import com.qian.usercenter.model.request.UserLoginRequest;
import com.qian.usercenter.model.request.UserRegisterRequest;
import com.qian.usercenter.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import com.qian.usercenter.comment.Utils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static com.qian.usercenter.constant.Constant.*;

@RestController
@RequestMapping("/user")
@CrossOrigin("http://localhost:3000/")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 响应用户注册请求
     * @param userRegisterRequest
     * @return 返回用户注册信息
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)) {
            return null;
        }
        return  userService.userRegister(userAccount,userPassword,checkPassword);
    }

    /**
     * 响应用户登录请求
     * @param userLoginRequest
     * @param request
     * @return 返回用户登录信息
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)) {
            return null;
        }
        return  userService.userLogin(userAccount,userPassword,request);
    }

    /**
     * 获取登录用户的信息
     * @param request
     * @return 返回脱敏的用户信息
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object user= request.getSession().getAttribute(USER_LOGIN_STATE);
        User user1 =(User) user;
        if (user1 ==null) {
            return null;
        }
        Long id = user1.getId();
        User byId = userService.getById(id);
        User safetyUser = userService.getSafetyUser(byId);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 用户管理——用户查询
     * @param userName
     * @param request
     * @return 返回查询的用户信息
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String userName,HttpServletRequest request){
        if (!Utils.isAdmin(request)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> eq = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userName)) {
            eq.like("userName",userName);
        }
        List<User> userList = userService.list(eq);
        List<User> collect = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    /**
     * 根据标签查询用户
     * @param tags
     * @return
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchByTags(@RequestParam List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tags);
        return ResultUtils.success(userList);
    }

    /**
     * 为当前用户推荐其他用户
     * @param pageSize
     * @param pageNumber
     * @param request
     * @return
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> RecommendUser(Long pageSize,Long pageNumber,HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String userKey = String.format("yupao:usercenter:recommend:%s", loginUser.getId());
        //在缓存中查询
        ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
        Page<User> recommendUser = (Page<User>) stringObjectValueOperations.get(userKey);
        if (recommendUser == null) {
            QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
            Page<User> recommendUser1 = userService.page(new Page<>(pageNumber,pageSize), objectQueryWrapper);
            try {
                stringObjectValueOperations.set(userKey,recommendUser1,30000, TimeUnit.MILLISECONDS);
            }catch (Exception e) {
                log.error("redis缓存存储错误",e);
            }
            return ResultUtils.success(recommendUser1);
        }
        return ResultUtils.success(recommendUser);
    }
    /**
     * 用户管理——用户更新
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request){
        //1 校验是否为空
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        int integerBaseResponse = userService.updateUser(user,loginUser);
        return ResultUtils.success(integerBaseResponse);
    }

    /**
     * 用户管理——用户删除
     * @param userId
     * @param request
     * @return 返回boolean类型
     */
   @PostMapping("/delete")
    public BaseResponse<Boolean> userDelete(@RequestBody long userId,HttpServletRequest request){
       if(!Utils.isAdmin(request)) {
           return ResultUtils.error(ErrorCode.PARAMS_ERROR);
       }
        QueryWrapper<User> eq = new QueryWrapper<>();
        if (userId <=0) {
             return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
       boolean b = userService.removeById(userId);
        return ResultUtils.success(b);
   }

    /**
     * 用户推出登录
     * @param request
     * @return 返回 Integer
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
       if (request == null) {
           return ResultUtils.error(ErrorCode.PARAMS_ERROR);
       }
       return userService.userLogout(request);
    }
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUser(Integer num,HttpServletRequest request) {
        if (num<=0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        List<User> userList = userService.matchUsers(num, loginUser);
        return ResultUtils.success(userList);
    }

}
