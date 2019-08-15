package com.throwsnew.springbootstudy.beanfactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午6:14 <br/>
 * Desc:
 */

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class})
public class Application {

    @Autowired
    ServiceA serviceA;
    @Autowired
    Hello hello;
}
