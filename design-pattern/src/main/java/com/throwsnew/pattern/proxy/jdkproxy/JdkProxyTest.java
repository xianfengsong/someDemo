package com.throwsnew.pattern.proxy.jdkproxy;

import com.throwsnew.pattern.proxy.DefaultServiceImpl;
import com.throwsnew.pattern.proxy.Service;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * author Xianfeng <br/>
 * date 19-6-10 上午11:12 <br/>
 * Desc:
 * 创建简单的动态代理，输出class文件(复制到了resources下)
 */
public class JdkProxyTest {

    private Service serviceProxied;

    /**
     * 用junit执行 会生成5个代理类($Proxy5.class),除了Service还包含了junit的4个接口代理
     *
     * @param args null
     */
    public static void main(String[] args) {
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        JdkProxyTest test = new JdkProxyTest();
        test.testCreate();
        test.testRun();
    }


    public void testCreate() {
        serviceProxied = (Service) Proxy
                .newProxyInstance(Service.class.getClassLoader(), new Class[]{Service.class},
                        new ServiceProxyHandler(new DefaultServiceImpl()));
    }


    public void testRun() {
        System.out.println(serviceProxied.serve());
    }

    static class ServiceProxyHandler implements InvocationHandler {

        private Service serviceImpl;

        ServiceProxyHandler(Service serviceImpl) {
            this.serviceImpl = serviceImpl;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getReturnType().equals(String.class)) {
                return "Proxy:" + method.invoke(serviceImpl, args);
            } else {
                for (int i = 0; i < args.length; i++) {
                    if (((Integer) args[i]) < 0) {
                        args[i] = 0;
                    }
                }
                return method.invoke(serviceImpl, args);
            }
        }
    }
}
