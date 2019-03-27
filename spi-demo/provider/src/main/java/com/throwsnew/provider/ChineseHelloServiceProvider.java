package com.throwsnew.provider;

import com.throwsnew.service.HelloService;
import com.throwsnew.service.HelloServiceProvider;

/**
 * author Xianfeng <br/>
 * date 19-3-27 下午4:25 <br/>
 * Desc:
 */
public class ChineseHelloServiceProvider implements HelloServiceProvider {

    @Override
    public HelloService getHelloService(String language) {
        if ("ZH".equals(language)) {
            return new HelloServiceImpl();
        } else {
            return null;
        }
    }

    class HelloServiceImpl implements HelloService {

        @Override
        public void hello() {
            System.out.println("你好");
        }
    }
}
