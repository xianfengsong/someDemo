package com.throwsnew.pattern.responsibilitychain.servletdemo;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * author Xianfeng <br/>
 * date 19-3-18 下午8:27 <br/>
 * Desc: 加密过滤
 */
public class EncryptionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("EncryptionFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println(this.getClass().getSimpleName() + "\tworking");
        chain.doFilter(request, response);
        System.out.println(this.getClass().getSimpleName() + "\tfinish: ***");
    }

    @Override
    public void destroy() {

    }
}
