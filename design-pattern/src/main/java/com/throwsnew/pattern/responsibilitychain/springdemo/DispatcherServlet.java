package com.throwsnew.pattern.responsibilitychain.springdemo;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author Xianfeng <br/>
 * date 19-4-16 上午10:59 <br/>
 * Desc:
 */
public class DispatcherServlet implements Servlet {

    /**
     * 获得request对应的handler（spring中的controller）
     *
     * @param req 请求
     * @return 随便返回一个对象
     */
    private Object getHandler(ServletRequest req) {
        return new Object();
    }

    private HandlerExecutionChain getExecutionChain(ServletRequest req) {
        Object handler = getHandler(req);
        HandlerExecutionChain chain = new HandlerExecutionChain(handler);
        chain.addInterceptor(new LoginInterceptor());
        return chain;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HandlerExecutionChain chain = getExecutionChain(req);
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpResp = (HttpServletResponse) res;

        try {

            if (!chain.applyPreHandle(httpReq, httpResp)) {
                return;
            }
            //得到ModelAndView（spring中由HandlerAdapter处理，这里省略）
            Object modelView = new Object();
            chain.applyPostHandle(httpReq, httpResp, modelView);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                chain.triggerAfterCompletion(httpReq, httpResp, e);
            } catch (Exception e1) {
                throw new ServletException(e1);
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //do nothing
    }


    @Override
    public ServletConfig getServletConfig() {
        //do nothing

        return null;
    }


    @Override
    public String getServletInfo() {
        //do nothing

        return null;
    }

    @Override
    public void destroy() {
        //do nothing

    }
}
