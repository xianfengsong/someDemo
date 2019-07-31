package com.throwsnew.springbootstudy.beanfactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * author Xianfeng <br/>
 * date 19-7-24 下午8:13 <br/>
 * Desc:
 */
@Component
public class Hello {

    @Value("hello")
    private String msg;

    private ServiceA serviceA;

    @Autowired
    public Hello(ServiceA serviceA) {
        this.serviceA = serviceA;
    }
}
