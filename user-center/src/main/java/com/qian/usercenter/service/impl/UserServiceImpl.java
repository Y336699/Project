package com.qian.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qian.usercenter.comment.*;
import com.qian.usercenter.comment.BaseResponse;
import com.qian.usercenter.job.exception.BusinessException;
import com.qian.usercenter.model.domain.User;
import com.qian.usercenter.service.UserService;
import com.qian.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static com.qian.usercenter.constant.Constant.USER_LOGIN_STATE;

/**
* @author Yu
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-02-26 18:06:51
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    //盐值，加密密码
    private static final String SALT = "qian";
    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return 返回用户id
     */
    @Override
    public BaseResponse<Long> userRegister(String userAccount, String userPassword, String checkPassword) {
        //校验不为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        if (userAccount.length() < 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度小于3");
        }
        if (userPassword.length() < 6 || checkPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于6");
        }

        //账户不能包含特殊字符
        String regex = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘：；“”’。，、？]";
        Matcher matcher = Pattern.compile(regex).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }
        //账户不能相同
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户已存在");
        }
        //密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        //加密
        String encryptuserPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptuserPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "插入错误");
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return 返回用户信息
     */
    @Override
    public BaseResponse<User> userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验不为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        if (userAccount.length() < 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度小于3");
        }
        if (userPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于6");
        }
        //账户不能包含特殊字符
        String regex = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘：；“”’。，、？]";
        Matcher matcher = Pattern.compile(regex).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }
        //查询密码是否相同
        String encryptuserPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptuserPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        //用户脱敏
        User safetyUser = this.getSafetyUser(user);
        //记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 获取当前用户登录信息
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户未登录");
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (attribute == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = (User) attribute;
        return user;
    }

    /**
     * 管理员实现用户更新
     *
     * @param user
     * @param userLogin
     * @return
     */
    @Override
    public int updateUser(User user, User userLogin) {
        Long id = user.getId();
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!Utils.isAdmin(userLogin) && id != userLogin.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User user1 = userMapper.selectById(id);
        if (user1 == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userMapper.updateById(user);
    }

    /**
     * 根据标签查询用户
     *
     * @param list
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(userQueryWrapper);
        Gson gson = new Gson();
        return userList.stream().filter(user -> {
            String tags = user.getTags();
            Set<String> string = gson.fromJson(tags, new TypeToken<Set<String>>() {
            }.getType());
            if (!list.contains(string)) {
                return false;
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 获取当前登录用户的用户信息
     *
     * @param request
     * @return
     */
    @Override
    public User getUserLogin(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object user = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return (User) user;
    }

    /**
     * 用户注销
     *
     * @param request
     * @return 返回 1
     */
    @Override
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return ResultUtils.success(1);
    }

    /**
     * 用户脱敏函数
     *
     * @param originUser
     * @return 返回脱敏后的用户信息
     */
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    @Deprecated
    public List<User> searchUsersByTages(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String a : list) {
            queryWrapper.like("tags", a);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public List<User> matchUsers(Integer number, User loginUser) {
      /*  QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("id","tags");
        userQueryWrapper.isNotNull("tags");
        List<User> list = this.list(userQueryWrapper);
        String loginUserTags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tags1 = gson.fromJson(loginUserTags, new TypeToken<List<String>>() {
        }.getType());
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据为空");
        }
        SortedMap<Integer,Integer> machMap = new TreeMap<>();
        for (int i =0;i<list.size();i++) {
            String tags = list.get(i).getTags();
            if (tags.isEmpty()) {
                continue;
            }
            List<String> tags2 = gson.fromJson(tags, new TypeToken<List<String>>() {
            }.getType());
            int distance = AlgorithmUtil.minDistance(tags1, tags2);
            machMap.put(i,distance);
        }

        ArrayList<User> users = new ArrayList<>();
        int i =0;
        for (Map.Entry<Integer,Integer> entry:machMap.entrySet()) {
            if (i>number) {
                break;
            }
            User user = list.get(i);
            System.out.println(user.getId() + ":" + entry.getKey() + ":" + entry.getValue());
            users.add(user);
            i++;
        }
        return users;
    }*/
        List<User> userList = this.list();
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        System.out.println(tagList);
        // 用户列表的下表 => 相似度
        SortedMap<Integer, Long> indexDistanceMap = new TreeMap<>();
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            //无标签的
            if (StringUtils.isBlank(userTags)) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            //计算分数
            long distance = AlgorithmUtil.minDistance(tagList, userTagList);
            indexDistanceMap.put(i, distance);
        }
        //下面这个是打印前num个的id和分数
        List<User> userListVo = new ArrayList<>();
        int i = 0;
        for (Map.Entry<Integer, Long> entry : indexDistanceMap.entrySet()) {
            if (i > number) {
                break;
            }
            User user = userList.get(entry.getKey());
            System.out.println(user.getId() + ":" + entry.getKey() + ":" + entry.getValue());
            userListVo.add(user);
            i++;
        }
        return userListVo;
    }
}
