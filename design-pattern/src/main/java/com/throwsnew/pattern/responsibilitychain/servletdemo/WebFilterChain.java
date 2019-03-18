package com.throwsnew.pattern.responsibilitychain.servletdemo;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * author Xianfeng <br/>
 * date 19-3-18 下午8:25 <br/>
 * Desc:
 */
public class WebFilterChain implements FilterChain {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

    }
}
