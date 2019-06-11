package com.throwsnew.pattern.proxy.cglibproxy;

import com.throwsnew.pattern.proxy.DefaultServiceImpl;
import com.throwsnew.pattern.proxy.Service;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;

/**
 * author Xianfeng <br/>
 * date 19-6-10 下午8:16 <br/>
 * Desc: cgLib 代理的基础api测试
 */
public class CgLibProxyTest {

    @Test
    public void noCallbackTest() {
        //让cglib 输出字节码到resources文件夹
        String sourcePath = "/root/workspace/someDemo/design-pattern/src/main/resources";
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, sourcePath);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(DefaultServiceImpl.class);
        enhancer.setCallback(NoOp.INSTANCE);
        Service service = (Service) enhancer.create();
        String result = service.serve();
        Assert.assertEquals("fail", "default serve you", result);
    }

    /**
     * 测试为添加MethodInterceptor callback 拦截代理方法的执行
     */
    @Test
    public void callbackTest() {
        //让cglib 输出字节码到resources文件夹
        String sourcePath = "/root/workspace/someDemo/design-pattern/src/main/resources";
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, sourcePath);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(DefaultServiceImpl.class);
        enhancer.setCallback(new MethodInterceptor() {
            /**
             *
             * @param o the proxy object
             * @param method intercepted Method
             * @param args arguments of the method
             * @param methodProxy the proxy used to invoke the original method
             * @return Object
             * @throws Throwable Throwable
             */
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy)
                    throws Throwable {
                if (method.getReturnType().equals(Integer.class) && args.length != 0) {
                    for (int i = 0; i < args.length; i++) {
                        if ((int) args[i] < 0) {
                            args[i] = Math.abs((int) args[i]);
                        }
                    }
                }
                //如果不调用invokeSuper就会循环调用 导致栈溢出了
//                return methodProxy.invoke(o,args);
                System.out.println("before");
                Object result = methodProxy.invokeSuper(o, args);
                System.out.println("after");
                return result;
            }
        });
        Service service = (Service) enhancer.create();
        int result = service.serve(-1, -1);
        Assert.assertEquals("fail", 2, result);
    }

    /**
     * 测试callback过滤器，对serve()和serve(int,int)分配不同的callback
     */
    @Test
    public void callbackFilterTest() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(DefaultServiceImpl.class);
        enhancer.setCallbacks(new MethodInterceptor[]{
                (o, method, objects, methodProxy) -> {
                    System.out.println("String result!");
                    return methodProxy.invokeSuper(o, objects);
                },
                (o, method, objects, methodProxy) -> {
                    System.out.println("Integer result!");
                    return methodProxy.invokeSuper(o, objects);
                }});

        enhancer.setCallbackFilter(new CallbackFilter() {
            private static final int STRING_CB = 0;
            private static final int INTEGER_CB = 1;

            /**
             * Specify which callback to use for the method being invoked
             * @param method the method being invoked.
             * @return the callback index in the callback array for this method
             */
            @Override
            public int accept(Method method) {
                if (method.getReturnType().equals(Integer.class)) {
                    return INTEGER_CB;
                }
                return STRING_CB;
            }
        });
        Service service = (Service) enhancer.create();
        service.serve();
        service.serve(1, 2);
    }

}
