package com.throwsnew.springbootstudy.beanfactory;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午6:14 <br/>
 * Desc:
 */

@SpringBootApplication
public class Application {

    @Bean
    public Hello hello() {
        return new Hello();
    }

    public class Hello {

    }
}
