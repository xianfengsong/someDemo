package com.throwsnew.springbootstudy.beanfactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午6:14 <br/>
 * Desc:
 */

@SpringBootApplication
public class Application {

    @Autowired
    ServiceA serviceA;
    @Bean
    public Hello hello() {
        return new Hello();
    }

    class Hello {
    }
}
