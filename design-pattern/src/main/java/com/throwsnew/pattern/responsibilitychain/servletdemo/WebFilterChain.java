package com.throwsnew.pattern.responsibilitychain.servletdemo;

import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * author Xianfeng <br/>
 * date 19-3-18 下午8:25 <br/>
 * Desc:
 */
public class WebFilterChain implements FilterChain {

    private List<Filter> filters;
    private int currentIndex = 0;
    private Servlet servlet;

    public WebFilterChain(List<Filter> filters, Servlet servlet) {
        this.filters = filters;
        this.servlet = servlet;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (currentIndex < filters.size()) {
            Filter currentFilter = filters.get(currentIndex++);
            currentFilter.doFilter(request, response, this);
        } else {
            //执行servlet
            servlet.service(request, response);
        }
    }
}
