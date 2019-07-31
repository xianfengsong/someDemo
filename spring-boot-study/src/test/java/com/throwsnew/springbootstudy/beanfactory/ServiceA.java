package com.throwsnew.springbootstudy.beanfactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author Xianfeng <br/>
 * date 19-7-22 下午3:44 <br/>
 * Desc:
 */
@Service
public class ServiceA {
    @Autowired
    ServiceB serviceB;
    public ServiceA() {
        Thread.dumpStack();
    }
}
