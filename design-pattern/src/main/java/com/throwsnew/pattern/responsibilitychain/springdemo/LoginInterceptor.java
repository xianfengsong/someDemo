package com.throwsnew.pattern.responsibilitychain.springdemo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

/**
 * author Xianfeng <br/>
 * date 19-4-16 上午10:43 <br/>
 * Desc:
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userName = (String) request.getAttribute("name");
        if (StringUtils.isEmpty(userName)) {
            System.out.println("login fail");
            return false;
        } else {
            System.out.println(userName + " log in");
            return true;
        }
    }
}
