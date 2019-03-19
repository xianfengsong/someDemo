package com.throwsnew.pattern.responsibilitychain.servletdemo;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.util.StringUtils;

/**
 * author Xianfeng <br/>
 * date 19-3-18 下午8:26 <br/>
 * Desc: 认证过滤
 */
public class AuthFilter implements Filter {

    FilterConfig filterConfig;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        System.out.println("AuthFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String userName = (String) request.getAttribute("name");
        if (StringUtils.isEmpty(userName)) {
            System.out.println(this.getClass().getSimpleName() + "user auth fail!");
        } else {
            System.out.println(this.getClass().getSimpleName() + " auth success!");
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
