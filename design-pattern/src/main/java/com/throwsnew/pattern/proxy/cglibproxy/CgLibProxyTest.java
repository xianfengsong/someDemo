package com.throwsnew.pattern.proxy.cglibproxy;

import com.throwsnew.pattern.proxy.DefaultServiceImpl;
import com.throwsnew.pattern.proxy.Service;
import org.junit.Test;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;

/**
 * author Xianfeng <br/>
 * date 19-6-10 下午8:16 <br/>
 * Desc:
 */
public class CgLibProxyTest {

    @Test
    public void test() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(DefaultServiceImpl.class);
        enhancer.setCallback(NoOp.INSTANCE);
        Service service = (Service) enhancer.create();
        String result = service.serve();
        System.out.println(result);
    }
}
