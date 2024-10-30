package com.qian.usercenter.service;

import com.qian.usercenter.comment.BaseResponse;
import com.qian.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Yu
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-02-26 18:06:51
*/
public interface UserService extends IService<User> {

    /**
     * 用户注释
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return 返回新用户id
     */
    //检验注册账户的合理性
    BaseResponse<Long> userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * @param userAccount
     * @param userPassword
     * @return 返回用户信息
     */
    BaseResponse<User> userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param orignUser
     * @return User（脱敏后的用户信息）
     */
    User getSafetyUser(User orignUser);

    /**
     * 获取当前用户登录信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 管理员用户更新
     * @param user
     * @param userLogin
     * @return
     */
    int updateUser(User user, User userLogin);

    /**
     * 用户退出登录
     *
     * @param request
     * @return int
     */
    BaseResponse<Integer> userLogout(HttpServletRequest request);

    /**
     * 根据标签查询用户
     * @param list
     * @return
     */
    List<User> searchUsersByTags(List<String> list);

    /**
     * 根据redis缓存获取用户信息
     * @param request
     * @return 返回用户信息
     */
    User getUserLogin(HttpServletRequest request);

    public List<User> matchUsers(Integer number,User loginUser);
}
