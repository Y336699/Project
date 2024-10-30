package com.qian.usercenter.comment;

import com.qian.usercenter.constant.Constant;
import com.qian.usercenter.model.domain.User;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;

import javax.servlet.http.HttpServletRequest;

import static com.qian.usercenter.constant.Constant.ADMIN_ROLE;
import static com.qian.usercenter.constant.Constant.USER_LOGIN_STATE;

public class Utils {
    public static boolean isAdmin(HttpServletRequest request) {
        Object user= request.getSession().getAttribute(USER_LOGIN_STATE);
        User user1 =(User) user;
        return user1 !=null || user1.getUserRole() == ADMIN_ROLE;
    }
    public static boolean isAdmin(User loginUser) {
        return loginUser !=null && loginUser.getUserRole() == ADMIN_ROLE;
    }

}
