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
 * date 19-3-18 下午8:26 <br/>
 * Desc: 日志过滤
 */
public class LoggerFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("LoggerFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        request.setAttribute("begin_mills", System.currentTimeMillis());
        System.out.println(this.getClass().getSimpleName() + "\tworking");
        chain.doFilter(request, response);

        long startMills = (long) request.getAttribute("begin_mills");
        System.out.println(String.format(this.getClass().getSimpleName() + "\t finish: log{runtime:%dms}",
                System.currentTimeMillis() - startMills));
    }

    @Override
    public void destroy() {

    }
}
