package com.throwsnew.provider;

import com.throwsnew.service.HelloService;
import com.throwsnew.service.HelloServiceProvider;

/**
 * author Xianfeng <br/>
 * date 19-3-27 下午4:31 <br/>
 * Desc:
 */
public class EnglishHelloServiceProvider implements HelloServiceProvider {

    @Override
    public HelloService getHelloService(String language) {
        if ("EN".equals(language)) {
            return new HelloServiceImpl();
        } else {
            return null;
        }
    }

    class HelloServiceImpl implements HelloService {

        @Override
        public void hello() {
            System.out.println("hello");
        }
    }
}
