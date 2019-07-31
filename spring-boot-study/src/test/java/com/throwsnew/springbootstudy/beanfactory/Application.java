package com.throwsnew.springbootstudy.beanfactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午6:14 <br/>
 * Desc:
 */

@SpringBootApplication
public class Application {
    @Autowired
    ServiceA serviceA;
    @Autowired
    Hello hello;
}
