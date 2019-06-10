package com.throwsnew.pattern.proxy.jdkproxy;

import com.throwsnew.pattern.proxy.Service;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.stream.Stream;

/**
 * author Xianfeng <br/>
 * date 19-6-10 下午4:15 <br/>
 * Desc:
 */
public class JdkProxyMultiInterfaceTest {

    public static void main(String[] args) {
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

//        AnotherService proxy = simpleProxy(AnotherService.class, new PassthroughInvocationHandler(new AnotherServiceImpl()),
//                AnotherService.class);
//        System.out.println(proxy.serve());
//        Service proxy2 = simpleProxy(Service.class, new PassthroughInvocationHandler(new DefaultServiceImpl()),
//                Service.class);
//        System.out.println(proxy2.serve());
        MergeService proxy3 = simpleProxy(MergeService.class, new PassthroughInvocationHandler(new MergeServiceImpl()),
                Service.class);
        System.out.println(proxy3.serve());
        System.out.println(proxy3.serve(1, 1));
    }

    public static <T> T simpleProxy(Class<? extends T> iface, InvocationHandler handler, Class<?>... otherIfaces) {
        Class<?>[] allInterfaces = Stream.concat(
                Stream.of(iface),
                Stream.of(otherIfaces))
                .distinct()
                .toArray(Class<?>[]::new);

        return (T) Proxy.newProxyInstance(
                iface.getClassLoader(),
                allInterfaces,
                handler);
    }

    interface MergeService extends AnotherService, Service {

    }

    interface AnotherService {

        String serve();
    }

    static class MergeServiceImpl implements MergeService {

        @Override
        public String serve() {
            return "????";
        }

    }

    static class AnotherServiceImpl implements AnotherService {

        @Override
        public String serve() {
            return "AnotherServiceImpl serve you";
        }
    }

    public static class PassthroughInvocationHandler implements InvocationHandler {

        private final Object target;

        public PassthroughInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(target, args);
        }

    }
}
