package com.throwsnew.application;

import com.throwsnew.service.HelloService;
import com.throwsnew.service.HelloServiceProvider;
import java.util.ServiceLoader;

/**
 * author Xianfeng <br/>
 * date 19-3-27 下午6:09 <br/>
 * Desc:
 */
public class Main {

    public static void main(String[] args) {
        String language = args[0];
        ServiceLoader<HelloServiceProvider> loader = ServiceLoader
                .load(HelloServiceProvider.class);
        for (HelloServiceProvider helloServiceProvider : loader) {
            HelloService helloService = helloServiceProvider.getHelloService(language);
            if (helloService != null) {
                helloService.hello();
            }
        }


    }
}
